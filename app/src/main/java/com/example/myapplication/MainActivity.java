package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DecryptorHelper Decrptor = new DecryptorHelper(this); // create an instance
        //AssetManager assetManager = this.getAssets();
//        try{
//            InputStream inputStream = assetManager.open("U900.jpg");
//
//            int fileSize = inputStream.available();
//
//            // Allocate a byte array to hold the file content
//            byte[] data = new byte[fileSize];
//
//            // Read the entire file into the byte array
//            int bytesRead = inputStream.read(data);
//           // Decrptor.w()
//
//        } catch (Exception error){
//            Log.d("error in opening file","error",error);
//        }

		//encrypted string =sa+zqx4k2BROlCAtPi1GNZce+quYwYY1u5fzb6Gn5REoZzsTc6ymkJXUoWA3dddZ
		//string is the ID of the question or explanation here its 1
		//127 is the key (control)


		/*
		* 
		* <?xml version='1.0' encoding='utf-8' standalone='yes' ?>
		<map>
			<string name="ActivationCode">a29d0e0df3b79aaf124b9d072cc08d7b4fcfee4749eeb45e34046169c65f952d5a5c679c1d01fcf06b89e2b0fc054a4614f9827aa3c4b901f513d916342942554bca63147935af38abbd135c2d97d947f6c3fc75710ab49ae434943cca12cdda341a0c5de0da6e2f698b07be169ecb9f3089b7355c3ae19adeb9ff0d954ca142</string>
			<string name="DS">E35A51818A6D9D8CF4069F5ADD740BE1B284380AEB0FC6DBF41E1A1D411F67A9A374179A5C1F899A830FCA85AD59C2E81BAA2890DFC34D2DE1B49AC91D8A94870E803CCCD5D30C42FA0717BCFD884FFC142ACA8B32DD991EC0E1AF2C8666E1C2DF3A629668B172EA8652F946403AFA8A</string>
		</map>

		* 
		*/
		//DecryptorHelper.B(bundle.getString("explanation"), string, "127");
        String test= Decrptor.B("=sa+zqx4k2BROlCAtPi1GNZce+quYwYY1u5fzb6Gn5REoZzsTc6ymkJXUoWA3dddZ", "1", "127");
        Log.d("Result:",test);
    }
}