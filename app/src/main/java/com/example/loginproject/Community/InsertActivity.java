package com.example.loginproject.Community;

import android.Manifest;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loginproject.Community.adapter.PhotoAdapter;
import com.example.loginproject.Community.model.FeedDTO;
import com.example.loginproject.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class InsertActivity extends AppCompatActivity {

    private final int REQUEST_TAKE_PHOTO = 1;
    private final int FROM_ALBUM = 2;

    int flag;

    String mCurrentPhotoPath;
    Uri imgUri;

    FeedDTO dto;

    EditText newTitle;
    EditText newMainText;
    ImageButton btnImgAdd;
    TextView photoNum;

    FirebaseDatabase database;
    DatabaseReference databaseReference;

    FirebaseStorage storage = FirebaseStorage.getInstance();

    RadioGroup radioGroup;

    RecyclerView photoView;
    PhotoAdapter adapter;
    ArrayList<Uri>  photoUriList = new ArrayList<>();

    ArrayList<Bitmap> photoList = new ArrayList<>();
    ArrayList<Uri> photoUpdate = new ArrayList<>();


    ArrayList<String> newImgList = null;
    ArrayList<String> filenameList = null;

    RelativeLayout progressBarLayout;

    String userName;
    String userIdToken;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                System.out.println("권한 설정 완료");
            }
            else{
                System.out.println("권한 설정 요청");
                ActivityCompat.requestPermissions(InsertActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},1 );
            }
        }

        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference().child("DB");
        userIdToken = CommunityActivity.getUserIdToken();
        userName = CommunityActivity.getUserName();

        newTitle = findViewById(R.id.newTitle);
        newMainText = findViewById(R.id.newMainText);
        btnImgAdd = findViewById(R.id.btnImgAdd);
        radioGroup = findViewById(R.id.radioGroup);
        photoNum = findViewById(R.id.photoNum);


        progressBarLayout = findViewById(R.id.progressbarLayout);
        progressBarLayout.setVisibility(View.INVISIBLE);


        photoView = findViewById(R.id.insertPhotoView);
        photoView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));

        Toolbar toolbar = findViewById(R.id.insertToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        if(intent.getSerializableExtra("dto")!=null){
            dto = (FeedDTO) intent.getSerializableExtra("dto");
            System.out.println(dto);
            getSupportActionBar().setTitle("내 글 수정");
            newTitle.setText(dto.getTitle());
            newMainText.setText(dto.getMainText());
            if(dto.getImageList() != null && dto.getImageList().size() != 0) {
                photoNum.setText(dto.getImageList().size() + "/4");
            }
            if(dto.getImageList()!= null){

                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageReference = storage.getReference().child(dto.getUserID());
                if(storageReference == null){
                    Toast.makeText(InsertActivity.this, "저장소에 사진이 없습니다.", Toast.LENGTH_SHORT).show();
                }else{
                    for(int i =0; i<dto.getImageList().size(); i++) {
                        final int index;
                        index = i;
                        System.out.println(dto.getImageList().get(index));
                        StorageReference loadImageUrl = storageReference.child(dto.getImageList().get(index));
                        System.out.println(loadImageUrl);
                        loadImageUrl.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                photoUpdate.add(uri);
                                photoUriList.add(uri);
                                adapter.notifyDataSetChanged();
                            }

                        });
                    }
                    adapter = new PhotoAdapter(photoUriList, photoNum);
                    photoView.setAdapter(adapter);
                }
            }

        }else{
            getSupportActionBar().setTitle("새 글 작성");
        }

        btnImgAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (photoList.size() < 4) {
                    final PopupMenu popupMenu = new PopupMenu(InsertActivity.this, view);
                    getMenuInflater().inflate(R.menu.camera_menu, popupMenu.getMenu());

                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            if(item.getItemId() == R.id.menu_takePhoto){
                                flag = 0;
                                takePhoto();

                            }else if(item.getItemId() == R.id.menu_Album){
                                flag = 1;
                                selectAlbum();
                            }else{

                            }
                            return false;
                        }
                    });
                    popupMenu.show();
                }else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(InsertActivity.this);
                    builder.setTitle("사진초과");
                    builder.setMessage("사진은 4장까지 첨부할 수 있습니다!");

                    builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });

                    builder.create().show();
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.insertmenu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
            case R.id.btnInsertOK:{
                if(dto != null){
                    noticeMessage("수정");
                }else{
                    noticeMessage("입력");
                }
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void noticeMessage(String type){
        AlertDialog.Builder builder = new AlertDialog.Builder(InsertActivity.this);
        builder.setTitle(type);
        builder.setMessage(type+"하시겠습니까?");
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

               if(type.equals("수정")){

                   int num = 0;

                   ArrayList<String> deleteList = new ArrayList<>();

                   if(photoUriList != null && photoUriList.size() != 0) {
                       newImgList = new ArrayList<>();
                           for(int j=0; j<photoUpdate.size(); j++){
                               if(photoUriList.contains(photoUpdate.get(j))){
                                   newImgList.add(dto.getImageList().get(j));
                                   num++;
                               }else{
                                   deleteList.add(dto.getImageList().get(j));
                               }
                       }

                       for (int i = 0; i < deleteList.size(); i++) {
                           StorageReference storageRef = storage.getReference().child(userIdToken + "/" + deleteList.get(i));
                           storageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                               @Override
                               public void onSuccess(Void aVoid) {

                                   System.out.println("삭제 성공");
                               }
                           }).addOnFailureListener(new OnFailureListener() {
                               @Override
                               public void onFailure(@NonNull Exception exception) {
                                   System.out.println("삭제 실패");
                               }
                           });
                       }

                      if(num < photoUriList.size()) {
                          for (int i = num; i < photoUriList.size(); i++) {
                              String filename = System.currentTimeMillis() + "";
                              newImgList.add(filename);
                              final int index;
                              index = i;
                              // 스토리지 저장소경로 수정
                              StorageReference storageRef = storage.getReference().child(userIdToken + "/" + filename);
                              UploadTask uploadTask;

                              Uri file = photoUriList.get(i);

                              uploadTask = storageRef.putFile(file);

                              uploadTask.addOnFailureListener(new OnFailureListener() {
                                  @Override
                                  public void onFailure(@NonNull Exception e) {
                                      System.out.println("업로드 실패");
                                      e.printStackTrace();
                                  }
                              }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                  @Override
                                  public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                      System.out.println("업로드 성공");
                                      if (index == photoUriList.size() - 1) {
                                          dto.setImageList(newImgList);
                                          dto.setTitle(newTitle.getText().toString());
                                          dto.setMainText(newMainText.getText().toString());

                                          databaseReference.child("Feeds").child(dto.getNo() + "").setValue(dto).addOnSuccessListener(new OnSuccessListener<Void>() {
                                              @Override
                                              public void onSuccess(Void unused) {
                                                  progressBarLayout.setVisibility(View.INVISIBLE);
                                                  Toast.makeText(InsertActivity.this, "수정됨", Toast.LENGTH_SHORT).show();
                                                  Intent intent = new Intent(InsertActivity.this, DetailActivity.class);
                                                  intent.putExtra("dto", dto);
                                                  startActivity(intent);
                                                  finish();
                                              }
                                          });
                                      }
                                  }
                              }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                  @Override
                                  public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {

                                      progressBarLayout.setVisibility(View.VISIBLE);
                                  }
                              });
                          }
                      }else{
                          dto.setImageList(newImgList);
                          dto.setTitle(newTitle.getText().toString());
                          dto.setMainText(newMainText.getText().toString());

                          databaseReference.child("Feeds").child(dto.getNo()+"").setValue(dto).addOnSuccessListener(new OnSuccessListener<Void>() {
                              @Override
                              public void onSuccess(Void unused) {
                                  Toast.makeText(InsertActivity.this, "수정됨",Toast.LENGTH_SHORT).show();
                                  Intent intent = new Intent(InsertActivity.this, DetailActivity.class);
                                  intent.putExtra("dto", dto);
                                  startActivity(intent);
                                  finish();
                              }
                          });
                      }
                   }else {
                        if(dto.getImageList() != null && dto.getImageList().size() != 0){
                           for (int i = 0; i < dto.getImageList().size(); i++) {
                               // 스토리지 저장소경로 수정
                               StorageReference storageRef = storage.getReference().child(userIdToken + "/" + dto.getImageList().get(i));
                               storageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                   @Override
                                   public void onSuccess(Void aVoid) {

                                       System.out.println("삭제 성공");
                                   }
                               }).addOnFailureListener(new OnFailureListener() {
                                   @Override
                                   public void onFailure(@NonNull Exception exception) {
                                       System.out.println("삭제 실패");
                                   }
                               });
                           }
                       }

                       dto.setImageList(newImgList);
                       dto.setTitle(newTitle.getText().toString());
                       dto.setMainText(newMainText.getText().toString());

                       databaseReference.child("Feeds").child(dto.getNo() + "").setValue(dto).addOnSuccessListener(new OnSuccessListener<Void>() {
                           @Override
                           public void onSuccess(Void unused) {
                               Toast.makeText(InsertActivity.this, "수정됨", Toast.LENGTH_SHORT).show();
                               Intent intent = new Intent(InsertActivity.this, DetailActivity.class);
                               intent.putExtra("dto", dto);
                               startActivity(intent);
                               finish();
                           }
                       });
                   }
               }else if(type.equals("입력")){
                   databaseReference.child("Feeds").child("Sequence").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                       @Override
                       public void onComplete(@NonNull Task<DataSnapshot> task) {
                           if(!task.isSuccessful()){
                               System.out.println("실패");
                           }else {
                               if (adapter != null && adapter.getItemCount() != 0) {
                                   filenameList = new ArrayList<>();
                                   for (int i = 0; i < photoUriList.size(); i++) {
                                       final int index;
                                       index = i;
                                       String filename = System.currentTimeMillis() + "";
                                       filenameList.add(filename);

                                       // 스토리지 저장소경로 수정
                                       StorageReference storageRef = storage.getReference().child(userIdToken + "/" + filename);
                                       UploadTask uploadTask;

                                       Uri file = photoUriList.get(i);

                                       uploadTask = storageRef.putFile(file);

                                       uploadTask.addOnFailureListener(new OnFailureListener() {
                                           @Override
                                           public void onFailure(@NonNull Exception e) {
                                               System.out.println("업로드 실패");
                                               e.printStackTrace();
                                           }
                                       }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                           @Override
                                           public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                               System.out.println("업로드 성공");
                                               if(index == photoUriList.size()-1) {


                                                   long seq = (Long) task.getResult().getValue();

                                                   RadioButton rb = (RadioButton) findViewById(radioGroup.getCheckedRadioButtonId());
                                                   int feedType = ((rb.getText().toString()).equals("운동") ? 2 : 1);

                                                   FeedDTO feed = new FeedDTO((int) seq, userIdToken, userName, newTitle.getText().toString(), newMainText.getText().toString()
                                                           , feedType, currentTime(), filenameList);

                                                   databaseReference.child("Feeds").child("Sequence").setValue(seq + 1);
                                                   databaseReference.child("Feeds").child(seq + "").setValue(feed).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                       @Override
                                                       public void onSuccess(Void unused) {
                                                           progressBarLayout.setVisibility(View.INVISIBLE);
                                                           Toast.makeText(InsertActivity.this, "입력됨", Toast.LENGTH_SHORT).show();
                                                           finish();
                                                       }
                                                   });
                                               }
                                           }
                                       }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                           @Override
                                           public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {

                                               progressBarLayout.setVisibility(View.VISIBLE);
                                           }
                                       });
                                   }
                               } else {
                                   long seq = (Long) task.getResult().getValue();

                                   RadioButton rb = (RadioButton) findViewById(radioGroup.getCheckedRadioButtonId());
                                   int feedType = ((rb.getText().toString()).equals("운동") ? 2 : 1);

                                   FeedDTO feed = new FeedDTO((int) seq, userIdToken, userName, newTitle.getText().toString(), newMainText.getText().toString()
                                           , feedType, currentTime(), filenameList);

                                   databaseReference.child("Feeds").child("Sequence").setValue(seq + 1);
                                   databaseReference.child("Feeds").child(seq + "").setValue(feed).addOnSuccessListener(new OnSuccessListener<Void>() {
                                       @Override
                                       public void onSuccess(Void unused) {
                                           Toast.makeText(InsertActivity.this, "입력됨", Toast.LENGTH_SHORT).show();
                                           finish();
                                       }
                                   });
                               }

                           }
                       }
                   });
               }
            }
        });

        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(InsertActivity.this, "취소되었습니다.",Toast.LENGTH_SHORT).show();
            }
        });

        builder.create().show();
    }
    public String currentTime(){
        long now = System.currentTimeMillis();
        Date date = new Date(now);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String getTime = sdf.format(date);
        return getTime;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        System.out.println("onRequestPermissionResult");
        if(grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
            System.out.println("Permission : "+ permissions[0] + "was "+ grantResults[0]);
        }
    }

    public void takePhoto(){
        // 사진촬영
        System.out.println("사진 촬영");

        String state = Environment.getExternalStorageState();

        if(Environment.MEDIA_MOUNTED.equals(state)){
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if(intent.resolveActivity(getPackageManager())!=null){
                File photoFile = null;

                try {
                    // 이미지 생성 함수
                    photoFile = createImageFile();
                }catch (IOException e){
                    e.printStackTrace();
                }

                if(photoFile != null){
                    Uri providerURI = FileProvider.getUriForFile(this, "com.example.loginproject.fileprovider",photoFile);
                    imgUri = providerURI;
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, providerURI);
                    startActivityForResult(intent, REQUEST_TAKE_PHOTO);
                }
            }
        }else{
            System.out.println("저장공간 접근 불가능");

        }
    }

    // 이미지 생성
    public File createImageFile() throws IOException{
        String imgFileName = System.currentTimeMillis()+"";
        File imageFile = null;
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        if(!storageDir.exists()){
            // 스토리지 없으면 만듦
            System.out.println("storage 존재하지 않음 : "+storageDir.toString());
            storageDir.mkdirs();
        }

        System.out.println("storage 존재함 : "+ storageDir.toString());
        imageFile = File.createTempFile(imgFileName, ".jpg",storageDir);
        mCurrentPhotoPath = imageFile.getAbsolutePath();

        return imageFile;

    }

    // 찍은 사진 갤러리 저장
    public void galleryAddPic(){
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        sendBroadcast(mediaScanIntent);
        Toast.makeText(InsertActivity.this, "사진 저장", Toast.LENGTH_SHORT).show();
    }


    // 앨범선택 클릭
    public void selectAlbum(){
        //앨범에서 이미지 가져옴

        //앨범 열기
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");

        startActivityForResult(intent, FROM_ALBUM);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode != RESULT_OK){
            return;
        }

        switch (requestCode) {

            case FROM_ALBUM: {
                // 앨범 가져오기

                if (data.getData() != null) {
                    if (data.getClipData() == null) {
                        if (photoUriList.size() <= 4) {
                            System.out.println("sing Image choice : " + String.valueOf(data.getData()));
                            Uri imageUri = data.getData();
                            System.out.println(imageUri);
                            photoUriList.add(imageUri);

                            try {
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                                photoList.add(bitmap);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

//                            adapter = new PhotoAdapter(photoList, photoNum);
                            adapter = new PhotoAdapter(photoUriList, photoNum);
                            photoView.setAdapter(adapter);

                        } else {
                            noticePhoto();
                        }


                    } else {
                        ClipData clipData = data.getClipData();
                        System.out.println("multi Image Choice : " + String.valueOf(clipData.getItemCount()));

                        if (clipData.getItemCount() > 4) {
                            Toast.makeText(getApplicationContext(), "사진은 4장까지 선택 가능합니다.", Toast.LENGTH_LONG).show();
                        } else {
                            System.out.println("multi choice");

                            for (int i = 0; i < clipData.getItemCount(); i++) {
                                if (photoUriList.size() < 4) {
                                    Uri imageUri = clipData.getItemAt(i).getUri();
                                    photoUriList.add(imageUri);
                                    System.out.println(imageUri);
                                    try {
                                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                                        photoList.add(bitmap);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    noticePhoto();
                                }
                            }
//                            adapter = new PhotoAdapter(photoList, photoNum);
                            adapter = new PhotoAdapter(photoUriList, photoNum);
                            photoView.setAdapter(adapter);
                        }
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "이미지를 선택하지 않았습니다.", Toast.LENGTH_LONG).show();
                }

                break;
            }

            case REQUEST_TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    if (photoUriList.size() < 4) {

                        galleryAddPic();
                        File file = new File(mCurrentPhotoPath);
                        Bitmap bitmap;
                        if (Build.VERSION.SDK_INT >= 29) {
                            ImageDecoder.Source source = ImageDecoder.createSource(getContentResolver(), Uri.fromFile(file));
                            try {
                                bitmap = ImageDecoder.decodeBitmap(source);
                                if (bitmap != null) {
                                    photoList.add(bitmap);
                                    photoUriList.add(Uri.fromFile(file));
//                                    adapter = new PhotoAdapter(photoList, photoNum);
                                    adapter = new PhotoAdapter(photoUriList, photoNum);
                                    photoView.setAdapter(adapter);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            try {
                                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.fromFile(file));
                                if (bitmap != null) {
                                    photoList.add(bitmap);
                                    photoUriList.add(Uri.fromFile(file));
//                                    adapter = new PhotoAdapter(photoList, photoNum);
                                    adapter = new PhotoAdapter(photoUriList, photoNum);
                                    photoView.setAdapter(adapter);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }else{
                        noticePhoto();
                    }
                    break;
                }
        }
        photoNum.setText(photoUriList.size()+"/4");
    }

    public void noticePhoto(){
        AlertDialog.Builder builder = new AlertDialog.Builder(InsertActivity.this);
        builder.setTitle("사진초과");
        builder.setMessage("사진은 4장까지 첨부할 수 있습니다!");

        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.create().show();
    }

}
