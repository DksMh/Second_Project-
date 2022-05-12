package com.example.loginproject.Food;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.loginproject.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

public class ListFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";

    private String mParam1;
    private String mParam2;
    private String mParam3;

    public ListFragment() {
    }

    public static ListFragment newInstance(String param1, String param2, String param3) {
        ListFragment fragment = new ListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        args.putString(ARG_PARAM3, param3);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            mParam3 = getArguments().getString(ARG_PARAM3);
        }

    }

    FirebaseAuth mFirebaseAuth;
    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;
    List<Places> placesList = new ArrayList<>();
    ListView listView;

    ArrayList<String> keyList = new ArrayList<>();

    static MyAdapter myAdapter;


    static String searchResult;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_list, container, false);
        listView = view.findViewById(R.id.listview);

        mFirebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = mFirebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("DB");

        databaseReference.child("placesinfo").orderByChild("idToken").equalTo(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                placesList.clear();
                keyList.clear();

                for(DataSnapshot ds : snapshot.getChildren()){
                    Places place =ds.getValue(Places.class);

                    System.out.println(ds.getValue());
                    System.out.println(ds.getKey());

                    keyList.add(ds.getKey());
                    placesList.add(place);
                }
                myAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        myAdapter = new MyAdapter(getActivity(), placesList, keyList);
        listView.setAdapter(myAdapter);
        return view;

    }

    public class MyAdapter extends BaseAdapter {
        Context context;
        List<Places> stringList;
        TextView txtPlace;
        ImageView imgPlace;

        Button btnUpdate;
        Button btnDelete;
        Button btnMap;

        List<String> keylist;

        FirebaseStorage storage;

        public MyAdapter(Context context, List<Places> stringList, List<String> list) {
            this.context = context;
            this.stringList = stringList;
            this.keylist = list;
        }

        @Override
        public int getCount() {
            return stringList.size();
        }

        @Override
        public Object getItem(int i) {
            return i;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            view = LayoutInflater.from(context).inflate(R.layout.places_layout, viewGroup, false);
            txtPlace = view.findViewById(R.id.txtCity);
            imgPlace = view.findViewById(R.id.imgPlace);
            txtPlace.setText("가게이름 : " + stringList.get(position).getName() +
                    "\n가게주소 : " + stringList.get(position).getAddress()
            );

            Glide.with((FoodMainActivity) getActivity()).load(placesList.get(position).getImage()).into(imgPlace);

            btnDelete = view.findViewById(R.id.btnDelete);
            btnUpdate = view.findViewById(R.id.btnUpdate);

            storage = FirebaseStorage.getInstance();

            btnMap = view.findViewById(R.id.btnMap);

            btnMap.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String str = placesList.get(position).getAddress();
                    searchResult = placesList.get(position).getName();
                    MapsFragment.editText.setText(str);

                    FoodMainActivity.viewPager.setCurrentItem(3);
                }
            });

            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String keyString = keylist.get(position);
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("삭제");
                    builder.setMessage("삭제할래요?");

                    builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(context, "확인", Toast.LENGTH_SHORT).show();

                            databaseReference = firebaseDatabase.getReference("DB").child("placesinfo").child(keyString);

                            storage.getReference().child("images").child(keyString).delete().addOnSuccessListener(new OnSuccessListener<Void>() { //이미지 스토리지 삭제
                                @Override
                                public void onSuccess(Void unused) {
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                }
                            });

                            databaseReference.setValue(null);
                        }
                    });

                    builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(context, "삭제 취소되었습니다.", Toast.LENGTH_SHORT).show();
                        }
                    });

                    builder.show();
                }
            });

            btnUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Places place0 = stringList.get(position);

                    Toast.makeText(context, stringList.get(position).getName() + " 수정", Toast.LENGTH_SHORT).show();

                    // 1. key, value 확인
                    System.out.println(position+ " 나 여기 잇어요오오오옹");
                    System.out.println("key : " + keylist.get(position));
                    System.out.println("value : " + stringList.get(position).toString());
                    System.out.println(position+ " 나 여기 잇어요오오오옹");
                    String keyString = keylist.get(position);

                    // 2. dialog 확인
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("수정");
                    builder.setMessage("내용을 입력하세요!");

                    TextView textName = new TextView(context);
                    TextView textAddress = new TextView(context);

                    textName.setText("가게이름 : ");
                    textAddress.setText("가게주소 : ");

                    final EditText newName = new EditText(context);
                    final EditText newAddress = new EditText(context);

                    LinearLayout linearLayout = new LinearLayout(context);

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

                    LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    LinearLayout.LayoutParams params3 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                    linearLayout.setOrientation(LinearLayout.VERTICAL);

                    LinearLayout innerLayout1 = new LinearLayout(context);

                    params2.setMargins(40, 5, 20, 5);
                    innerLayout1.addView(textName, params2);
                    innerLayout1.addView(newName, params3);
                    LinearLayout innerLayout2 = new LinearLayout(context);

                    innerLayout2.addView(textAddress, params2);
                    innerLayout2.addView(newAddress, params3);

                    linearLayout.addView(innerLayout1, params2);
                    linearLayout.addView(innerLayout2, params2);

                    builder.setView(linearLayout);

                    newName.setText(place0.getName());
                    newAddress.setText(place0.getAddress());

                    builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(context, "확인", Toast.LENGTH_SHORT).show();

                            place0.setName(newName.getText().toString());
                            place0.setAddress(newAddress.getText().toString());

                            databaseReference = firebaseDatabase.getReference("DB").child("placesinfo").child(keyString);
                            databaseReference.setValue(place0);
                        }
                    });

                    builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(context, "수정 취소되었습니다.", Toast.LENGTH_SHORT).show();
                        }
                    });
                    builder.show();
                }
            });
            return view;
        }
    }



}