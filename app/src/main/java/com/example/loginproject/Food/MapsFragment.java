package com.example.loginproject.Food;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.loginproject.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import android.location.Address;
import android.location.Geocoder;
import android.widget.Button;
import android.widget.EditText;
import java.util.List;
import java.io.IOException;


public class MapsFragment extends Fragment {
    private GoogleMap mMap;
    private Button button;
    static EditText editText;
    private Geocoder geocoder;
    String str1;

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;
            geocoder = new Geocoder((FoodMainActivity)getActivity());


            button.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    str1 = editText.getText().toString();

                    mapMarker(str1);
                }
            });

            // Add a marker in Sydney and move the camera
            LatLng gangnam = new LatLng(37.498095, 127.027610);
            mMap.addMarker(new MarkerOptions().position(gangnam).title("Marker in Gangnam"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(gangnam));
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_maps, container, false);
        button = view.findViewById(R.id.button);
        editText = view.findViewById(R.id.editText);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }

    public void mapMarker(String str1){
        List<Address> addressList = null;

        try {
            // editText에 입력한 텍스트(주소, 지역, 장소 등)을 지오 코딩을 이용해 변환
            addressList = geocoder.getFromLocationName(
                    str1, // 주소
                    100); // 최대 검색 결과 개수
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 콤마를 기준으로 split
        String[] splitStr = addressList.get(0).toString().split(",");
        String address = splitStr[0].substring(splitStr[0].indexOf("\"") + 1, splitStr[0].length() - 2); // 주소

        String latitude = splitStr[10].substring(splitStr[10].indexOf("=") + 1); // 위도
        String longitude = splitStr[12].substring(splitStr[12].indexOf("=") + 1); // 경도

        // 좌표(위도, 경도) 생성
        LatLng point = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
        // 마커 생성
        MarkerOptions mOptions = new MarkerOptions();
        if(ListFragment.searchResult != null){
            mOptions.title(ListFragment.searchResult); // name 받아와서 마커에 찍기
            ListFragment.searchResult = null;
        } else {
            mOptions.title(str1);
        }
        mOptions.snippet(address);
        mOptions.position(point);
        // 마커 추가
        mMap.addMarker(mOptions);
        // 해당 좌표로 화면 줌
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point, 15));
        editText.setText("");
    }
}