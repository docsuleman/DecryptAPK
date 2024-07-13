package com.example.myapplication;


import android.content.Context;
import android.content.res.AssetManager;
import android.os.FileUtils;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.provider.Settings;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.UUID;
import java.util.zip.GZIPInputStream;
import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.lang3.StringUtils;



public class DecryptorHelper {



    public  Context vbcontext;
    public Cipher Cipher;

    public byte n = (byte) 0xFF;




    public DecryptorHelper(Context context) {
        this.vbcontext = context;
    }

    public String B(String str, String str2, String str3) {
        Log.d("Running:", "B");

        if (!str3.equals("127")) {
            return null;
        }

        Log.d("l() output is ",l());
        String str5 = w(l()).split("::")[1];
        byte[] decode = Base64.decode(str, Base64.DEFAULT);
        for (int length = str2.length(); length < 8; length++) {
            str2 = str2 + StringUtils.SPACE;
        }

        try {
            return new String(W0(F0(str5.toCharArray(), str2.getBytes("UTF-8"), new byte[]{17, 115, 105, 102, 103, 104, 111, 107, 108, 122, 120, 119, 118, 98, 110, 109}, decode)));
        } catch (Exception e) {
            //FirebaseCrashlytics.getInstance().log(e.toString());
            Log.d("Error Location:","error in B");
            return null;
        }
    }

    public String l() {

        int i = 165;
        Log.d("Running:", "l");




        String string=Settings.Secure.getString(this.vbcontext.getContentResolver(), "android_id");
        Log.d("android_id",string);
        //f280cabcd71f2dfc
        if (string == null) {
           string = "soheilvb";
        }
        //Settings.Secure.getString(this.vbcontext.getContentResolver(), "android_id");

        String string2 = "E35A51818A6D9D8CF4069F5ADD740BE1B284380AEB0FC6DBF41E1A1D411F67A9A374179A5C1F899A830FCA85AD59C2E81BAA2890DFC34D2DE1B49AC91D8A94870E803CCCD5D30C42FA0717BCFD884FFC142ACA8B32DD991EC0E1AF2C8666E1C2DF3A629668B172EA8652F946403AFA8A";
        String j = j(string2, "127", string + "30");

        if (j != null) {
            Log.d("J is=",j);
            return j;
        }else{
            Log.d("J is=","nulled");
        }

        byte[] bArr = {31, 32, 33, 34, 35, 36, 37, 38, 39, 30, 31, 32, 33, 34, 35, 36};
        byte[] bArr2 = {122, 13, 11, 120, 32, 34, 56, 78, 21, 24, 76, 21, 65, 32, 76, 31, 24, 64, 23};
        try {
            byte[] output=g("hs;d,hghdk[;".toCharArray(), bArr2, bArr, (UUID.randomUUID().toString() + ",,,,," + String.valueOf(i)).getBytes("UTF-8"));
            Log.d("Out of G function:",String.valueOf(output));
            String replace = e(output).replace("\n", "XX");
            Log.d("REplace:",replace);
            String n = n(replace, "127", string + "30");
            // if (PreferenceManager.getDefaultSharedPreferences(this.context).contains("DS")) {
            //     PreferenceManager.getDefaultSharedPreferences(this.context).edit().remove("DS").commit();
            // }
            // PreferenceManager.getDefaultSharedPreferences(this.context).edit().putString("DS", n).commit();
            return replace;
        } catch (Exception e3) {
            //FirebaseCrashlytics.d().g(e3);
            Log.d("Error Location:","error in l second cipher",e3);

            return null;
        }
    }

    public String j(String str, String str2, String str3) {
        Log.d("Running:", "j");

        Log.d("input in J", str+"-"+str2+"-"+str3);


        if (!str2.equals("127")) {
            return null;
        }
        byte[] bArr = {31, 32, 33, 34, 35, 36, 37, 38, 39, 30, 31, 32, 33, 34, 35, 36};
        try {
            return new String(h(str3.toCharArray(), new byte[]{122, 12, 11, 120, 32, 34, 56, 78, 21, 24, 76, 21, 65, 32, 76, 31, 24, 64, 23}, bArr, t(str)));
        } catch (Exception unused) {
            Log.d("Error Location:","error in j",unused);
            return null;


        }
    }



    public byte[] a() {
        Log.d("Running:", "a");

        String str;
        //String string = PreferenceManager.getDefaultSharedPreferences(this.vbcontext).getString("ActivationCode", "");
        String string = q();
        if (string.length() == 0) {
            return null;
        }else{


            Log.d("Activate Code from a", string);
        }

        return t(string);
//        try {
//            String outputl=l();
//            Log.d("outputl is working from a()",outputl);
//            str = new String(h(outputl.toCharArray(), new byte[]{122, 12, 11, 120, 32, 34, 56, 78, 21, 24, 76, 21, 65, 32, 76, 31, 24, 64, 23}, new byte[]{117, 115, 111, 102, 103, 104, 111, 111, 108, 122, 120, 119, 111, 91, 110, 109}, t(string)));
//            //str = new String(h(outputl.toCharArray(), new byte[]{122, 12, 11, 120, 32, 34, 56, 78, 21, 24, 76, 21, 65, 32, 76, 31, 24, 64, 23}, new byte[] {31, 32, 33, 34, 35, 36, 37, 38, 39, 30, 31, 32, 33, 34, 35, 36};
//
//            Log.d("Working a:", str);
//        } catch (Exception unused) {
//            str = null;
//            Log.d("Error Location:","error in a",unused);
//
//        }
//        if (str != null) {
//            return t(string);
//        }
//        //PreferenceManager.getDefaultSharedPreferences(this.vbcontext).edit().remove("ActivationCode").commit();
//        return null;
    }

    public byte[] t(String str) {
        Log.d("Running:", "t");

        String trim = str.trim();
        int length = trim.length();
        byte[] bArr = new byte[(length / 2)];
        for (int i = 0; i < length; i += 2) {
            bArr[i / 2] = (byte) ((Character.digit(trim.charAt(i), 16) << 4) + Character.digit(trim.charAt(i + 1), 16));
        }
        Log.d("t output is:", String.valueOf(bArr));
        return bArr;
    }

    public String q() {
        Log.d("Running:", "q");

        String ActivationCode = "a29d0e0df3b79aaf124b9d072cc08d7b4fcfee4749eeb45e34046169c65f952d5a5c679c1d01fcf06b89e2b0fc054a4614f9827aa3c4b901f513d916342942554bca63147935af38abbd135c2d97d947f6c3fc75710ab49ae434943cca12cdda341a0c5de0da6e2f698b07be169ecb9f3089b7355c3ae19adeb9ff0d954ca142";
        //return PreferenceManager.getDefaultSharedPreferences(this.vbcontext).getString(ActivationCode);
        return ActivationCode;
    }

    public String w(String str) {
        Log.d("Running W input is:", str);

        String str2;
        //String q = q();
        // if (f20436g == q) {
        //     return f20435f;
        // }
        try {

            str2 = new String(h(str.toCharArray(), new byte[]{122, 12, 11, 120, 32, 34, 56, 78, 21, 24, 76, 21, 65, 32, 76, 31, 24, 64, 23}, new byte[]{117, 115, 111, 102, 103, 104, 111, 111, 108, 122, 120, 119, 111, 91, 110, 109}, a()));
        } catch (Exception unused) {
            Log.d("Error Location:","error in w", unused);
            str2 = null;
        }

        //FirebaseCrashlytics.d().r(TextUtils.split(str2.replace("||", "::"), "::")[9]);
        // f20435f = str2;
        // f20436g = q;
        return str2;

    }


//    public String e(byte[] bArr) {
//        Log.d("Running:", "e");
//        char[] cArr = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
//        char[] cArr2 = new char[(bArr.length * 2)];
//        for (int i = 0; i < bArr.length; i++) {
//            //potential issue
//
//            byte b2 = (byte) (bArr[i] & this.n);;
//            //byte b2 = bArr[i] & UnsignedBytes.f15572b;
//            //byte b2 = bArr[i] ;
//            Log.d("bArr_length,i,b2,cArr2_Length",String.valueOf(bArr.length)+"-"+String.valueOf(i)+"-"+String.valueOf(b2)+"-"+String.valueOf(cArr2.length));
//
//            int i2 = i * 2;
//            cArr2[i2] = cArr[b2 >>> 4];
//            cArr2[i2 + 1] = cArr[b2 & 15];
//        }
//
//        Log.d("e output is ",new String(cArr2));
//        return new String(cArr2);
//    }

    public String e(byte[] bArr) {

        Log.d("e input is running", Arrays.toString(bArr));

        StringBuilder sb = new StringBuilder();
        for (byte b : bArr) {
            sb.append(String.format("%02x", b));
        }

        Log.d("e out put is",sb.toString());
        return sb.toString();
    }

    public String n(String str, String str2, String str3) {
        Log.d("Running:", "n");
        if (!str2.equals("127")) {
            return null;
        }
        byte[] bArr = {31, 32, 33, 34, 35, 36, 37, 38, 39, 30, 31, 32, 33, 34, 35, 36};
        try {
            return e(g(str3.toCharArray(), new byte[]{122, 12, 11, 120, 32, 34, 56, 78, 21, 24, 76, 21, 65, 32, 76, 31, 24, 64, 23}, bArr, str.getBytes("UTF-8")));
        } catch (Exception unused) {
            Log.d("Error Location:","error in n");
            return null;
        }
    }

    public byte[] g(char[] cArr, byte[] bArr, byte[] bArr2, byte[] bArr3) throws Exception {
        Log.d("Running:", "g");
        Log.d("Input to g:", String.valueOf(cArr)+" - "+String.valueOf(bArr2)+" - "+ String.valueOf(bArr3));

        SecretKeySpec secretKeySpec = new SecretKeySpec(SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1").generateSecret(new PBEKeySpec(cArr, bArr, 19, 128)).getEncoded(), "AES");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(bArr2);
        Cipher instance = Cipher.getInstance("AES/CBC/PKCS7Padding");
        Log.d("Key/IV in G", String.valueOf(secretKeySpec)+" - "+ String.valueOf(ivParameterSpec)+" - "+String.valueOf(bArr3));

        instance.init(1, secretKeySpec, ivParameterSpec);
        byte[] output=instance.doFinal(bArr3);
        Log.d("returned from G:",String.valueOf(output));
        return output;
    }
    public byte[] h(char[] cArr, byte[] bArr, byte[] bArr2, byte[] bArr3)  throws Exception {
        Log.d("Running:", "h");

        Log.d("Input to h:", String.valueOf(cArr)+" - "+String.valueOf(bArr2)+" - "+ String.valueOf(bArr3));

        SecretKeySpec secretKeySpec = new SecretKeySpec(SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1").generateSecret(new PBEKeySpec(cArr, bArr, 19, 128)).getEncoded(), "AES");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(bArr2);
        Cipher instance = Cipher.getInstance("AES/CBC/PKCS7Padding");
        Log.d("Key/IV in h:",String.valueOf(secretKeySpec)+" - "+String.valueOf(ivParameterSpec)+" - "+String.valueOf(bArr3));
        instance.init(2, secretKeySpec, ivParameterSpec);
        byte[] output=instance.doFinal(bArr3);
        Log.d("h output is", String.valueOf(output));
        return output;
    }


    public byte[] F0(char[] cArr, byte[] bArr, byte[] bArr2, byte[] bArr3) throws Exception {
        Log.d("Running:", "F0");
        SecretKeySpec secretKeySpec = new SecretKeySpec(SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1").generateSecret(new PBEKeySpec(cArr, bArr, 19, 128)).getEncoded(), "AES");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(bArr2);
        Cipher instance = Cipher.getInstance("AES/CBC/PKCS7Padding");
        instance.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
        return instance.doFinal(bArr3);
    }

    public static byte[] W0(byte[] bArr) throws IOException {
        Log.d("Running:", "W0");

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bArr);
        GZIPInputStream gZIPInputStream = new GZIPInputStream(byteArrayInputStream);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = gZIPInputStream.read(buffer)) > 0) {
            byteArrayOutputStream.write(buffer, 0, len);
        }
        return byteArrayOutputStream.toByteArray();
    }

//    public void B4() {
//
//            Z0="filename";
//
//                try {
//                    AssetManager assetManager = context.getAssets();
//                    InputStream inputStream = assetManager.open("data.txt");
//                    byte[] w = w(FileUtils.readFileToByteArray(new File(Z0)), string, "127");
//                    //String Z02 = CompressHelper.Z0(this.c4, string, "base");
//
//
//                    FileUtils.writeByteArrayToFile(new File(Z02), w, false);
//                    new File(Z02).deleteOnExit();
//                } catch (Exception e2) {
//                    //FirebaseCrashlytics.d().g(e2);
//                    e2.printStackTrace();
//                }
//            }
//        }
//        this.y4 = V;
//    }
//
}


//bundle.getString("explanation") is actualy the encrypted string
//SAMPLE for above is =sa+zqx4k2BROlCAtPi1GNZce+quYwYY1u5fzb6Gn5REoZzsTc6ymkJXUoWA3dddZ
//string is the ID of the question or explanation
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







