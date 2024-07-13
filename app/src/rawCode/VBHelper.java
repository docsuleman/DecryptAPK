package com.example.myapplication;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.TextUtils;
import com.dd.plist.ASCIIPropertyListParser;
import com.dd.plist.NSDictionary;
import com.dd.plist.PropertyListParser;
import com.google.android.exoplayer2.metadata.icy.IcyHeaders;
import com.google.common.base.Ascii;
import com.google.common.primitives.SignedBytes;
import com.google.common.primitives.UnsignedBytes;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.itextpdf.text.DocWriter;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.UUID;
import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import net.imedicaldoctor.imd.Data.CompressHelper;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

public class VBHelper {

    /* renamed from: d  reason: collision with root package name */
    static String[] f20433d;

    /* renamed from: e  reason: collision with root package name */
    static String f20434e;

    /* renamed from: f  reason: collision with root package name */
    static String f20435f;

    /* renamed from: g  reason: collision with root package name */
    static String f20436g;

    /* renamed from: a  reason: collision with root package name */
    Context f20437a;

    /* renamed from: b  reason: collision with root package name */
    CompressHelper f20438b;

    /* renamed from: c  reason: collision with root package name */
    Bundle f20439c;

    public VBHelper(Context context) {
        this.f20437a = context;
    }

    public byte[] a() {
        String str;
        String string = PreferenceManager.getDefaultSharedPreferences(this.f20437a).getString("ActivationCode", "");
        if (string.length() == 0) {
            return null;
        }
        try {
            str = new String(h(l().toCharArray(), new byte[]{122, 12, 11, 120, 32, DocWriter.G2, 56, 78, Ascii.y, Ascii.B, 76, Ascii.y, 65, 32, 76, Ascii.I, Ascii.B, SignedBytes.f15570a, Ascii.A}, new byte[]{117, 115, 111, 102, 103, 104, 111, 111, 108, 122, 120, 119, 111, 91, 110, 109}, t(string)));
        } catch (Exception unused) {
            str = null;
        }
        if (str != null) {
            return t(string);
        }
        PreferenceManager.getDefaultSharedPreferences(this.f20437a).edit().remove("ActivationCode").commit();
        return null;
    }

    public Bundle b(byte[] bArr) {
        return d(bArr, (File) null);
    }

    public Bundle c(File file) {
        try {
            return d(FileUtils.readFileToByteArray(file), file);
        } catch (Exception e2) {
            FirebaseCrashlytics.d().g(e2);
            iMDLogger.f("analyzeVBE vbeFile", "Error in reading vbe file " + e2.toString());
            e2.printStackTrace();
            return null;
        }
    }

    public Bundle d(byte[] bArr, File file) {
        String str;
        Bundle bundle;
        if (this.f20438b == null) {
            this.f20438b = new CompressHelper(this.f20437a);
        }
        if (file != null) {
            try {
                str = f(file.getAbsolutePath(), (file.length() + file.lastModified()) + "");
            } catch (Exception e2) {
                iMDLogger.f("AnalyzeVBE", "Error in decrypting " + e2.getMessage());
                e2.printStackTrace();
                return null;
            }
        } else {
            str = null;
        }
        boolean z = true;
        if (str == null) {
            NSDictionary nSDictionary = (NSDictionary) PropertyListParser.h(new String(h(TextUtils.split(w(l()).replace("||", "::"), "::")[1].toCharArray(), "info.vb ".getBytes("UTF-8"), new byte[]{17, 115, 105, 102, 103, 104, 111, 107, 108, 122, 120, 119, 118, 98, 110, 109}, bArr)).replace("&", "&amp;").getBytes("UTF-8"));
            bundle = new Bundle();
            for (String str2 : nSDictionary.w()) {
                bundle.putString(str2, nSDictionary.S(str2).toString().replace("soheilvb", "&"));
            }
            if (file != null) {
                this.f20438b.t0(file.getAbsolutePath(), v(bundle), (file.length() + file.lastModified()) + "");
            }
        } else {
            bundle = k(str);
        }
        StringBuilder sb = new StringBuilder();
        if (bundle.containsKey("Version")) {
            z = x(bundle.getString("Name"), bundle.getString("Version"), sb);
            if (sb.toString().length() > 0) {
                bundle.putString("ExpDate", sb.toString());
            }
        }
        if (!z) {
            bundle.putString("Inactive", IcyHeaders.C2);
            if (u(bundle.getString("Type"))) {
                bundle.putString("Demo", IcyHeaders.C2);
            }
        }
        return bundle;
    }

    public String e(byte[] bArr) {
        char[] cArr = {'0', '1', PdfWriter.R3, PdfWriter.S3, PdfWriter.T3, PdfWriter.U3, PdfWriter.V3, PdfWriter.W3, '8', '9', 'A', ASCIIPropertyListParser.u, 'C', ASCIIPropertyListParser.t, 'E', 'F'};
        char[] cArr2 = new char[(bArr.length * 2)];
        for (int i = 0; i < bArr.length; i++) {
            byte b2 = bArr[i] & UnsignedBytes.f15572b;
            int i2 = i * 2;
            cArr2[i2] = cArr[b2 >>> 4];
            cArr2[i2 + 1] = cArr[b2 & 15];
        }
        return new String(cArr2);
    }

    public String f(String str, String str2) {
        Bundle s = s();
        if (s.containsKey(str)) {
            Bundle bundle = s.getBundle(str);
            if (bundle.getString("cachevalidation").equals(str2)) {
                return bundle.getString("cachecontent");
            }
            CompressHelper compressHelper = this.f20438b;
            String x0 = compressHelper.x0();
            compressHelper.q(x0, "Delete from cache where cachekey = '" + str + "'");
        }
        return null;
    }

    public byte[] g(char[] cArr, byte[] bArr, byte[] bArr2, byte[] bArr3) throws Exception {
        SecretKeySpec secretKeySpec = new SecretKeySpec(SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1").generateSecret(new PBEKeySpec(cArr, bArr, 19, 128)).getEncoded(), "AES");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(bArr2);
        Cipher instance = Cipher.getInstance("AES/CBC/PKCS7Padding");
        instance.init(1, secretKeySpec, ivParameterSpec);
        return instance.doFinal(bArr3);
    }

    public byte[] h(char[] cArr, byte[] bArr, byte[] bArr2, byte[] bArr3) throws Exception {
        SecretKeySpec secretKeySpec = new SecretKeySpec(SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1").generateSecret(new PBEKeySpec(cArr, bArr, 19, 128)).getEncoded(), "AES");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(bArr2);
        Cipher instance = Cipher.getInstance("AES/CBC/PKCS7Padding");
        instance.init(2, secretKeySpec, ivParameterSpec);
        return instance.doFinal(bArr3);
    }

    public String i(String str, String str2) {
        if (!str2.equals("127")) {
            return null;
        }
        byte[] bArr = {Ascii.I, 32, 33, DocWriter.G2, 35, 36, 37, 38, 39, Ascii.H, Ascii.I, 32, 33, DocWriter.G2, 35, 36};
        try {
            return new String(h("hs;d,hghdk[;ak".toCharArray(), new byte[]{122, 12, 11, 120, 32, DocWriter.G2, 56, 78, Ascii.y, Ascii.B, 76, Ascii.y, 65, 32, 76, Ascii.I, Ascii.B, SignedBytes.f15570a, Ascii.A}, bArr, t(str)));
        } catch (Exception unused) {
            return null;
        }
    }

    public String j(String str, String str2, String str3) {
        if (!str2.equals("127")) {
            return null;
        }
        byte[] bArr = {Ascii.I, 32, 33, DocWriter.G2, 35, 36, 37, 38, 39, Ascii.H, Ascii.I, 32, 33, DocWriter.G2, 35, 36};
        try {
            return new String(h(str3.toCharArray(), new byte[]{122, 12, 11, 120, 32, DocWriter.G2, 56, 78, Ascii.y, Ascii.B, 76, Ascii.y, 65, 32, 76, Ascii.I, Ascii.B, SignedBytes.f15570a, Ascii.A}, bArr, t(str)));
        } catch (Exception unused) {
            return null;
        }
    }

    public Bundle k(String str) {
        String[] splitByWholeSeparator = StringUtils.splitByWholeSeparator(str, "$$$");
        Bundle bundle = new Bundle();
        for (String splitByWholeSeparator2 : splitByWholeSeparator) {
            String[] splitByWholeSeparator3 = StringUtils.splitByWholeSeparator(splitByWholeSeparator2, ":::");
            bundle.putString(splitByWholeSeparator3[0], splitByWholeSeparator3[1]);
        }
        return bundle;
    }

    public String l() {
        int i = 0;
        try {
            i = this.f20437a.getPackageManager().getPackageInfo(this.f20437a.getPackageName(), 0).versionCode;
        } catch (Exception e2) {
            FirebaseCrashlytics.d().g(e2);
        }
        String string = Settings.Secure.getString(this.f20437a.getContentResolver(), "android_id");
        if (string == null) {
            string = "soheilvb";
        }
        if (PreferenceManager.getDefaultSharedPreferences(this.f20437a).contains("DS")) {
            String string2 = PreferenceManager.getDefaultSharedPreferences(this.f20437a).getString("DS", "");
            String j = j(string2, "127", string + "30");
            if (j != null) {
                return j;
            }
        }
        byte[] bArr = {Ascii.I, 32, 33, DocWriter.G2, 35, 36, 37, 38, 39, Ascii.H, Ascii.I, 32, 33, DocWriter.G2, 35, 36};
        byte[] bArr2 = {122, 13, 11, 120, 32, DocWriter.G2, 56, 78, Ascii.y, Ascii.B, 76, Ascii.y, 65, 32, 76, Ascii.I, Ascii.B, SignedBytes.f15570a, Ascii.A};
        try {
            String replace = e(g("hs;d,hghdk[;".toCharArray(), bArr2, bArr, (UUID.randomUUID().toString() + ",,,,," + String.valueOf(i)).getBytes("UTF-8"))).replace("\n", "XX");
            String n = n(replace, "127", string + "30");
            if (PreferenceManager.getDefaultSharedPreferences(this.f20437a).contains("DS")) {
                PreferenceManager.getDefaultSharedPreferences(this.f20437a).edit().remove("DS").commit();
            }
            PreferenceManager.getDefaultSharedPreferences(this.f20437a).edit().putString("DS", n).commit();
            return replace;
        } catch (Exception e3) {
            FirebaseCrashlytics.d().g(e3);
            return null;
        }
    }

    public String m(String str, String str2) {
        if (!str2.equals("127")) {
            return null;
        }
        byte[] bArr = {Ascii.I, 32, 33, DocWriter.G2, 35, 36, 37, 38, 39, Ascii.H, Ascii.I, 32, 33, DocWriter.G2, 35, 36};
        try {
            return e(g("hs;d,hghdk[;ak".toCharArray(), new byte[]{122, 12, 11, 120, 32, DocWriter.G2, 56, 78, Ascii.y, Ascii.B, 76, Ascii.y, 65, 32, 76, Ascii.I, Ascii.B, SignedBytes.f15570a, Ascii.A}, bArr, str.getBytes("UTF-8")));
        } catch (Exception unused) {
            return null;
        }
    }

    public String n(String str, String str2, String str3) {
        if (!str2.equals("127")) {
            return null;
        }
        byte[] bArr = {Ascii.I, 32, 33, DocWriter.G2, 35, 36, 37, 38, 39, Ascii.H, Ascii.I, 32, 33, DocWriter.G2, 35, 36};
        try {
            return e(g(str3.toCharArray(), new byte[]{122, 12, 11, 120, 32, DocWriter.G2, 56, 78, Ascii.y, Ascii.B, 76, Ascii.y, 65, 32, 76, Ascii.I, Ascii.B, SignedBytes.f15570a, Ascii.A}, bArr, str.getBytes("UTF-8")));
        } catch (Exception unused) {
            return null;
        }
    }

    public String o(byte[] bArr, String str) {
        if (!str.equals("127")) {
            return null;
        }
        byte[] bArr2 = {Ascii.I, 32, 33, DocWriter.G2, 35, 36, 37, 38, 39, Ascii.H, Ascii.I, 32, 33, DocWriter.G2, 35, 36};
        try {
            return e(g("hs;d,hghdk[;ak".toCharArray(), new byte[]{122, 12, 11, 120, 32, DocWriter.G2, 56, 78, Ascii.y, Ascii.B, 76, Ascii.y, 65, 32, 76, Ascii.I, Ascii.B, SignedBytes.f15570a, Ascii.A}, bArr2, bArr));
        } catch (Exception unused) {
            return null;
        }
    }

    public String p(String str, String str2) {
        for (int length = str2.length(); length < 8; length++) {
            str2 = str2 + StringUtils.SPACE;
        }
        try {
            return e(g("soheilvb'ghndhj,v".toCharArray(), str2.getBytes("UTF-8"), new byte[]{117, 115, 111, 102, 103, 104, 111, 111, 108, 122, 120, 119, 111, 91, 110, 109}, str.getBytes("UTF-8")));
        } catch (Exception e2) {
            FirebaseCrashlytics.d().g(e2);
            return null;
        }
    }

    public String q() {
        return PreferenceManager.getDefaultSharedPreferences(this.f20437a).getString("ActivationCode", "");
    }

    public void r() {
        String q = q();
        if (q != f20434e) {
            String[] split = TextUtils.split(w(l()).replace("||", "::"), "::");
            String str = split[1];
            String[] split2 = TextUtils.split(split[3], ",");
            f20434e = q;
            f20433d = split2;
        }
    }

    public Bundle s() {
        if (this.f20439c == null) {
            Bundle bundle = new Bundle();
            CompressHelper compressHelper = this.f20438b;
            ArrayList<Bundle> Y = compressHelper.Y(compressHelper.x0(), "Select * from cache");
            if (Y == null) {
                return bundle;
            }
            Iterator<Bundle> it2 = Y.iterator();
            while (it2.hasNext()) {
                Bundle next = it2.next();
                bundle.putBundle(next.getString("cachekey"), next);
            }
            this.f20439c = bundle;
        }
        return this.f20439c;
    }

    public byte[] t(String str) {
        String trim = str.trim();
        int length = trim.length();
        byte[] bArr = new byte[(length / 2)];
        for (int i = 0; i < length; i += 2) {
            bArr[i / 2] = (byte) ((Character.digit(trim.charAt(i), 16) << 4) + Character.digit(trim.charAt(i + 1), 16));
        }
        return bArr;
    }

    public boolean u(String str) {
        return TextUtils.split(w(l()).replace("||", "::"), "::")[6].equals("0");
    }

    public String v(Bundle bundle) {
        ArrayList arrayList = new ArrayList();
        for (String next : bundle.keySet()) {
            arrayList.add(next + ":::" + bundle.getString(next));
        }
        return StringUtils.join((Iterable<?>) arrayList, "$$$");
    }

    public String w(String str) {
        String str2;
        String q = q();
        if (f20436g == q) {
            return f20435f;
        }
        try {
            str2 = new String(h(str.toCharArray(), new byte[]{122, 12, 11, 120, 32, DocWriter.G2, 56, 78, Ascii.y, Ascii.B, 76, Ascii.y, 65, 32, 76, Ascii.I, Ascii.B, SignedBytes.f15570a, Ascii.A}, new byte[]{117, 115, 111, 102, 103, 104, 111, 111, 108, 122, 120, 119, 111, 91, 110, 109}, a()));
        } catch (Exception unused) {
            str2 = null;
        }
        if (str2 == null) {
            PreferenceManager.getDefaultSharedPreferences(this.f20437a).edit().remove("ActivationCode").commit();
            return null;
        }
        FirebaseCrashlytics.d().r(TextUtils.split(str2.replace("||", "::"), "::")[9]);
        f20435f = str2;
        f20436g = q;
        return str2;
    }

    public boolean x(String str, String str2, StringBuilder sb) {
        ArrayList arrayList = new ArrayList();
        Bundle bundle = new Bundle();
        r();
        for (String str3 : f20433d) {
            if (str3.contains("$$$")) {
                String str4 = StringUtils.splitByWholeSeparator(str3, "$$$")[0];
                if (str4.contains("-expired")) {
                    str4 = str4.replace("-expired", "");
                }
                arrayList.add(str4);
                bundle.putString(str4, StringUtils.splitByWholeSeparator(str3, "$$$")[1]);
            } else {
                arrayList.add(str3);
            }
        }
        if (!arrayList.contains(TtmlNode.r0)) {
            if (!arrayList.contains(str)) {
                return false;
            }
            if (bundle.containsKey(str)) {
                String string = bundle.getString(str);
                String format = new SimpleDateFormat("yyyyMMdd").format(new Date());
                sb.append(string);
                if (str2.length() == 6) {
                    string = string.substring(0, 6);
                    format = format.substring(0, 6);
                }
                if (string.compareTo(str2) >= 0) {
                    return !str.equals("uptodateonline") || string.compareTo(format) >= 0;
                }
                return false;
            }
        }
    }

    public Bundle y() {
        r();
        Bundle bundle = new Bundle();
        for (String str : f20433d) {
            if (str.contains("$$$")) {
                String str2 = StringUtils.splitByWholeSeparator(str, "$$$")[0];
                if (str2.contains("-expired")) {
                    str2 = str2.replace("-expired", "");
                }
                bundle.putString(str2, StringUtils.splitByWholeSeparator(str, "$$$")[1]);
            } else {
                bundle.putString(str, "0");
            }
        }
        return bundle;
    }
}
