package com.example.myapplication;


import android.content.Context;
import android.os.Bundle;
import androidx.exifinterface.media.ExifInterface;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.SequenceInputStream;
import java.util.Date;
import java.util.Iterator;
import java.util.Vector;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import net.imedicaldoctor.imd.Data.CompressHelper;
import net.imedicaldoctor.imd.Data.UnzipCompleted;
import net.lingala.zip4j.progress.ProgressMonitor;
import org.apache.commons.io.IOUtils;

public class Decompress {

    /* renamed from: a  reason: collision with root package name */
    private String f19844a;

    /* renamed from: b  reason: collision with root package name */
    private String f19845b;

    /* renamed from: c  reason: collision with root package name */
    Context f19846c;

    /* renamed from: d  reason: collision with root package name */
    CompressHelper f19847d;

    public Decompress(String str, String str2, Context context) {
        this.f19844a = str;
        this.f19845b = str2;
        this.f19846c = context;
        this.f19847d = new CompressHelper(context);
        a("");
    }

    private void a(String str) {
        File file = new File(this.f19845b + str);
        if (!file.isDirectory()) {
            file.mkdirs();
        }
    }

    public static byte[] b(String str, String str2) {
        ZipEntry nextEntry;
        try {
            FileInputStream fileInputStream = new FileInputStream(str);
            ZipInputStream zipInputStream = new ZipInputStream(new BufferedInputStream(fileInputStream));
            do {
                nextEntry = zipInputStream.getNextEntry();
                if (nextEntry == null) {
                    zipInputStream.close();
                    fileInputStream.close();
                    return null;
                }
            } while (!nextEntry.getName().equals(str2));
            byte[] byteArray = IOUtils.toByteArray((InputStream) zipInputStream);
            zipInputStream.close();
            return byteArray;
        } catch (Exception e2) {
            FirebaseCrashlytics.d().g(e2);
            iMDLogger.f("Error in unzip", e2.getLocalizedMessage());
            return null;
        }
    }

    public static byte[] c(String str, String str2, Bundle bundle) {
        Date date = new Date();
        try {
            Vector vector = new Vector(10);
            for (int i = 1; i < 11; i++) {
                vector.add(new FileInputStream(str + "." + i));
            }
            SequenceInputStream sequenceInputStream = new SequenceInputStream(vector.elements());
            ZipInputStream zipInputStream = new ZipInputStream(new BufferedInputStream(sequenceInputStream));
            String str3 = "";
            while (true) {
                try {
                    ZipEntry nextEntry = zipInputStream.getNextEntry();
                    if (nextEntry == null) {
                        break;
                    } else if (!nextEntry.isDirectory()) {
                        nextEntry.getName().toLowerCase().endsWith(str2);
                    } else if (str3.length() == 0) {
                        str3 = nextEntry.getName();
                    }
                } catch (Exception e2) {
                    FirebaseCrashlytics.d().g(e2);
                    return null;
                }
            }
            zipInputStream.close();
            sequenceInputStream.close();
            Iterator it2 = vector.iterator();
            while (it2.hasNext()) {
                try {
                    ((FileInputStream) it2.next()).close();
                } catch (Exception unused) {
                }
            }
            long seconds = TimeUnit.MILLISECONDS.toSeconds(new Date().getTime() - date.getTime());
            iMDLogger.f("Found file", "In " + seconds + " Seconds");
        } catch (Exception e3) {
            FirebaseCrashlytics.d().g(e3);
            iMDLogger.f("Error in unzip", e3.getLocalizedMessage() + " in " + "");
            e3.printStackTrace();
        }
        return null;
    }

    public static Observable<byte[]> d(final String str, final String str2) {
        return Observable.x1(new ObservableOnSubscribe<byte[]>() {
            public void a(@NonNull ObservableEmitter<byte[]> observableEmitter) throws Throwable {
                try {
                    InputStream inputStream = new ZipFile(str).getInputStream(new ZipEntry(str2));
                    byte[] byteArray = IOUtils.toByteArray(inputStream);
                    inputStream.close();
                    observableEmitter.onNext(byteArray);
                } catch (Exception e2) {
                    e2.printStackTrace();
                    iMDLogger.f("Error in unzip", e2.getLocalizedMessage());
                }
            }
        });
    }

    public static void e(String str, String str2, UnzipCompleted unzipCompleted) {
        try {
            InputStream inputStream = new ZipFile(str).getInputStream(new ZipEntry(str2));
            byte[] byteArray = IOUtils.toByteArray(inputStream);
            inputStream.close();
            unzipCompleted.b(byteArray);
        } catch (Exception e2) {
            FirebaseCrashlytics.d().g(e2);
            iMDLogger.f("Error in unzip", e2.getLocalizedMessage());
            unzipCompleted.a(e2.getLocalizedMessage());
        }
    }

    public String f(ObservableEmitter<Bundle> observableEmitter, String str) {
        try {
            net.lingala.zip4j.ZipFile zipFile = new net.lingala.zip4j.ZipFile(this.f19844a);
            if (zipFile.D()) {
                zipFile.R("imedicaldoctor".toCharArray());
            }
            Bundle bundle = new Bundle();
            bundle.putString("progress", "");
            bundle.putString("labelText", str);
            zipFile.S(true);
            ProgressMonitor A = zipFile.A();
            zipFile.o(this.f19845b);
            String str2 = "";
            while (A.i() == ProgressMonitor.State.BUSY) {
                PrintStream printStream = System.out;
                printStream.println("Percent Done: " + A.g());
                PrintStream printStream2 = System.out;
                printStream2.println("File: " + A.f());
                String format = String.format(TimeModel.D2, new Object[]{Integer.valueOf(A.g())});
                if (!format.equals(str2)) {
                    iMDLogger.f("Decompress", "Percent : " + format);
                    bundle.remove("progress");
                    bundle.putString("progress", format);
                    observableEmitter.onNext(bundle);
                    str2 = format;
                }
            }
            if (A.h() == ProgressMonitor.Result.SUCCESS) {
                return "0";
            }
            A.e().printStackTrace();
            return "0";
        } catch (Exception e2) {
            FirebaseCrashlytics.d().g(e2);
            iMDLogger.f("Error in unzip", e2.getLocalizedMessage() + " in " + "");
            e2.printStackTrace();
            return e2.getLocalizedMessage() + " in " + "";
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v7, resolved type: io.reactivex.rxjava3.core.ObservableEmitter<android.os.Bundle>} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v8, resolved type: io.reactivex.rxjava3.core.ObservableEmitter<android.os.Bundle>} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v17, resolved type: io.reactivex.rxjava3.core.ObservableEmitter<android.os.Bundle>} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v18, resolved type: io.reactivex.rxjava3.core.ObservableEmitter<android.os.Bundle>} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v19, resolved type: io.reactivex.rxjava3.core.ObservableEmitter<android.os.Bundle>} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v21, resolved type: io.reactivex.rxjava3.core.ObservableEmitter<android.os.Bundle>} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v22, resolved type: io.reactivex.rxjava3.core.ObservableEmitter<android.os.Bundle>} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v19, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v20, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v22, resolved type: java.util.ArrayList} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v21, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v30, resolved type: io.reactivex.rxjava3.core.ObservableEmitter<android.os.Bundle>} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v35, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v36, resolved type: io.reactivex.rxjava3.core.ObservableEmitter<android.os.Bundle>} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v34, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v36, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v40, resolved type: java.util.ArrayList} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v42, resolved type: java.util.ArrayList} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v44, resolved type: java.util.ArrayList} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v45, resolved type: java.util.ArrayList} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v37, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v38, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v46, resolved type: java.util.ArrayList} */
    /* JADX WARNING: type inference failed for: r2v33 */
    /* JADX WARNING: type inference failed for: r2v34 */
    /* JADX WARNING: type inference failed for: r2v44 */
    /* JADX WARNING: type inference failed for: r2v47 */
    /* JADX WARNING: type inference failed for: r3v44 */
    /* JADX WARNING: type inference failed for: r2v59 */
    /* JADX WARNING: type inference failed for: r2v60 */
    /* JADX WARNING: type inference failed for: r2v62 */
    /* JADX WARNING: type inference failed for: r2v63 */
    /* JADX WARNING: Code restructure failed: missing block: B:37:0x0152, code lost:
        if (r5 == false) goto L_0x0172;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:38:0x0154, code lost:
        r3 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:41:0x0159, code lost:
        if (r3 >= r6.size()) goto L_0x016f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:43:0x0165, code lost:
        r1 = r36;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:45:?, code lost:
        r1.f19847d.j(((android.os.Bundle) r6.get(r3)).getString(r14));
        r3 = r3 + 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:46:0x016f, code lost:
        r1 = r36;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:47:0x0172, code lost:
        r1 = r36;
        r3 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:49:0x0179, code lost:
        if (r3 >= r26.size()) goto L_0x0191;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:50:0x017b, code lost:
        r2 = r26;
        r1.f19847d.j(((android.os.Bundle) r2.get(r3)).getString(r14));
        r3 = r3 + 1;
        r26 = r2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:51:0x0191, code lost:
        r12.close();
        net.imedicaldoctor.imd.iMDLogger.f("Zip Completed", "In " + java.util.concurrent.TimeUnit.MILLISECONDS.toSeconds(new java.util.Date().getTime() - r23.getTime()) + " Seconds");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:52:0x01c5, code lost:
        return "0";
     */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.lang.String g(io.reactivex.rxjava3.core.ObservableEmitter<android.os.Bundle> r37, java.lang.String r38, android.app.Activity r39) {
        /*
            r36 = this;
            r1 = r36
            r2 = r37
            java.lang.String r3 = "/"
            java.lang.String r4 = "progress"
            java.lang.String r5 = ""
            java.util.ArrayList r6 = new java.util.ArrayList     // Catch:{ Exception -> 0x0483 }
            r6.<init>()     // Catch:{ Exception -> 0x0483 }
            java.util.ArrayList r7 = new java.util.ArrayList     // Catch:{ Exception -> 0x0483 }
            r7.<init>()     // Catch:{ Exception -> 0x0483 }
            java.util.Vector r0 = new java.util.Vector     // Catch:{ Exception -> 0x0483 }
            r8 = 10
            r0.<init>(r8)     // Catch:{ Exception -> 0x0483 }
            r9 = 0
            r11 = 1
        L_0x001e:
            r12 = 11
            java.lang.String r13 = "offset"
            java.lang.String r14 = "filePath"
            if (r11 >= r12) goto L_0x0067
            java.lang.StringBuilder r12 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0063 }
            r12.<init>()     // Catch:{ Exception -> 0x0063 }
            java.lang.String r15 = r1.f19844a     // Catch:{ Exception -> 0x0063 }
            r12.append(r15)     // Catch:{ Exception -> 0x0063 }
            java.lang.String r15 = "."
            r12.append(r15)     // Catch:{ Exception -> 0x0063 }
            r12.append(r11)     // Catch:{ Exception -> 0x0063 }
            java.lang.String r12 = r12.toString()     // Catch:{ Exception -> 0x0063 }
            android.os.Bundle r15 = new android.os.Bundle     // Catch:{ Exception -> 0x0063 }
            r15.<init>()     // Catch:{ Exception -> 0x0063 }
            java.io.File r8 = new java.io.File     // Catch:{ Exception -> 0x0063 }
            r8.<init>(r12)     // Catch:{ Exception -> 0x0063 }
            long r16 = r8.length()     // Catch:{ Exception -> 0x0063 }
            long r9 = r9 + r16
            r15.putString(r14, r12)     // Catch:{ Exception -> 0x0063 }
            r15.putLong(r13, r9)     // Catch:{ Exception -> 0x0063 }
            r6.add(r15)     // Catch:{ Exception -> 0x0063 }
            r7.add(r15)     // Catch:{ Exception -> 0x0063 }
            java.io.FileInputStream r8 = new java.io.FileInputStream     // Catch:{ Exception -> 0x0063 }
            r8.<init>(r12)     // Catch:{ Exception -> 0x0063 }
            r0.add(r8)     // Catch:{ Exception -> 0x0063 }
            int r11 = r11 + 1
            goto L_0x001e
        L_0x0063:
            r0 = move-exception
            r2 = r1
            goto L_0x0487
        L_0x0067:
            java.util.Enumeration r0 = r0.elements()     // Catch:{ Exception -> 0x0483 }
            java.io.SequenceInputStream r8 = new java.io.SequenceInputStream     // Catch:{ Exception -> 0x0483 }
            r8.<init>(r0)     // Catch:{ Exception -> 0x0483 }
            java.io.BufferedInputStream r0 = new java.io.BufferedInputStream     // Catch:{ Exception -> 0x0483 }
            r11 = 131072(0x20000, float:1.83671E-40)
            r0.<init>(r8, r11)     // Catch:{ Exception -> 0x0483 }
            com.google.common.io.CountingInputStream r8 = new com.google.common.io.CountingInputStream     // Catch:{ Exception -> 0x0483 }
            r8.<init>(r0)     // Catch:{ Exception -> 0x0483 }
            java.lang.String r0 = r1.f19844a     // Catch:{ Exception -> 0x0483 }
            java.lang.String r12 = ".zipp"
            boolean r0 = r0.contains(r12)     // Catch:{ Exception -> 0x0483 }
            if (r0 == 0) goto L_0x0092
            net.lingala.zip4j.io.inputstream.ZipInputStream r0 = new net.lingala.zip4j.io.inputstream.ZipInputStream     // Catch:{ Exception -> 0x0063 }
            java.lang.String r12 = "imedicaldoctor"
            char[] r12 = r12.toCharArray()     // Catch:{ Exception -> 0x0063 }
            r0.<init>((java.io.InputStream) r8, (char[]) r12)     // Catch:{ Exception -> 0x0063 }
            goto L_0x0097
        L_0x0092:
            net.lingala.zip4j.io.inputstream.ZipInputStream r0 = new net.lingala.zip4j.io.inputstream.ZipInputStream     // Catch:{ Exception -> 0x0483 }
            r0.<init>(r8)     // Catch:{ Exception -> 0x0483 }
        L_0x0097:
            r12 = r0
            java.util.Date r0 = new java.util.Date     // Catch:{ Exception -> 0x0483 }
            r0.<init>()     // Catch:{ Exception -> 0x0483 }
            java.util.Date r15 = new java.util.Date     // Catch:{ Exception -> 0x0483 }
            r15.<init>()     // Catch:{ Exception -> 0x0483 }
            android.os.Bundle r11 = new android.os.Bundle     // Catch:{ Exception -> 0x0483 }
            r11.<init>()     // Catch:{ Exception -> 0x0483 }
            r11.putString(r4, r5)     // Catch:{ Exception -> 0x0483 }
            r17 = r0
            java.lang.String r0 = "labelText"
            r18 = r5
            r5 = r38
            r11.putString(r0, r5)     // Catch:{ Exception -> 0x047e }
            java.io.File r0 = new java.io.File     // Catch:{ Exception -> 0x047e }
            java.lang.String r5 = r1.f19845b     // Catch:{ Exception -> 0x047e }
            r0.<init>(r5)     // Catch:{ Exception -> 0x047e }
            android.content.SharedPreferences r0 = android.preference.PreferenceManager.getDefaultSharedPreferences(r39)     // Catch:{ Exception -> 0x047e }
            java.lang.String r5 = "lessspace"
            r19 = r3
            r3 = 0
            boolean r5 = r0.getBoolean(r5, r3)     // Catch:{ Exception -> 0x047e }
            java.lang.Object r0 = r6.get(r3)     // Catch:{ Exception -> 0x047e }
            android.os.Bundle r0 = (android.os.Bundle) r0     // Catch:{ Exception -> 0x047e }
            long r20 = r0.getLong(r13)     // Catch:{ Exception -> 0x047e }
            r0 = r17
        L_0x00d5:
            java.util.Date r17 = new java.util.Date     // Catch:{ Exception -> 0x047e }
            r17.<init>()     // Catch:{ Exception -> 0x047e }
            long r22 = r17.getTime()     // Catch:{ Exception -> 0x047e }
            long r24 = r0.getTime()     // Catch:{ Exception -> 0x047e }
            r17 = r4
            long r3 = r22 - r24
            r39 = r0
            java.util.concurrent.TimeUnit r0 = java.util.concurrent.TimeUnit.MILLISECONDS     // Catch:{ Exception -> 0x047e }
            long r3 = r0.toSeconds(r3)     // Catch:{ Exception -> 0x047e }
            r22 = r13
            r13 = 1
            long r0 = (long) r13
            java.lang.String r13 = "Percent : "
            r23 = r15
            java.lang.String r15 = "%.2f"
            r24 = 4636737291354636288(0x4059000000000000, double:100.0)
            r26 = r7
            java.lang.String r7 = "Decompress"
            int r27 = (r3 > r0 ? 1 : (r3 == r0 ? 0 : -1))
            if (r27 <= 0) goto L_0x0146
            long r3 = r8.c()     // Catch:{ Exception -> 0x0141 }
            double r3 = (double) r3     // Catch:{ Exception -> 0x0141 }
            r28 = r0
            double r0 = (double) r9     // Catch:{ Exception -> 0x0141 }
            double r3 = r3 / r0
            double r3 = r3 * r24
            r1 = 1
            java.lang.Object[] r0 = new java.lang.Object[r1]     // Catch:{ Exception -> 0x0141 }
            java.lang.Double r1 = java.lang.Double.valueOf(r3)     // Catch:{ Exception -> 0x0141 }
            r3 = 0
            r0[r3] = r1     // Catch:{ Exception -> 0x0141 }
            java.lang.String r0 = java.lang.String.format(r15, r0)     // Catch:{ Exception -> 0x0141 }
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0141 }
            r1.<init>()     // Catch:{ Exception -> 0x0141 }
            r1.append(r13)     // Catch:{ Exception -> 0x0141 }
            r1.append(r0)     // Catch:{ Exception -> 0x0141 }
            java.lang.String r1 = r1.toString()     // Catch:{ Exception -> 0x0141 }
            net.imedicaldoctor.imd.iMDLogger.f(r7, r1)     // Catch:{ Exception -> 0x0141 }
            r1 = r17
            r11.remove(r1)     // Catch:{ Exception -> 0x0141 }
            r11.putString(r1, r0)     // Catch:{ Exception -> 0x0141 }
            if (r2 == 0) goto L_0x013a
            r2.onNext(r11)     // Catch:{ Exception -> 0x0141 }
        L_0x013a:
            java.util.Date r0 = new java.util.Date     // Catch:{ Exception -> 0x0141 }
            r0.<init>()     // Catch:{ Exception -> 0x0141 }
            r3 = r0
            goto L_0x014c
        L_0x0141:
            r0 = move-exception
            r2 = r36
            goto L_0x0480
        L_0x0146:
            r28 = r0
            r1 = r17
            r3 = r39
        L_0x014c:
            net.lingala.zip4j.model.LocalFileHeader r4 = r12.n()     // Catch:{ Exception -> 0x0433 }
            if (r4 != 0) goto L_0x01c6
            if (r5 == 0) goto L_0x0172
            r3 = 0
        L_0x0155:
            int r0 = r6.size()     // Catch:{ Exception -> 0x0141 }
            if (r3 >= r0) goto L_0x016f
            java.lang.Object r0 = r6.get(r3)     // Catch:{ Exception -> 0x0141 }
            android.os.Bundle r0 = (android.os.Bundle) r0     // Catch:{ Exception -> 0x0141 }
            java.lang.String r0 = r0.getString(r14)     // Catch:{ Exception -> 0x0141 }
            r1 = r36
            net.imedicaldoctor.imd.Data.CompressHelper r2 = r1.f19847d     // Catch:{ Exception -> 0x047e }
            r2.j(r0)     // Catch:{ Exception -> 0x047e }
            int r3 = r3 + 1
            goto L_0x0155
        L_0x016f:
            r1 = r36
            goto L_0x0191
        L_0x0172:
            r1 = r36
            r3 = 0
        L_0x0175:
            int r0 = r26.size()     // Catch:{ Exception -> 0x047e }
            if (r3 >= r0) goto L_0x0191
            r2 = r26
            java.lang.Object r0 = r2.get(r3)     // Catch:{ Exception -> 0x047e }
            android.os.Bundle r0 = (android.os.Bundle) r0     // Catch:{ Exception -> 0x047e }
            java.lang.String r0 = r0.getString(r14)     // Catch:{ Exception -> 0x047e }
            net.imedicaldoctor.imd.Data.CompressHelper r4 = r1.f19847d     // Catch:{ Exception -> 0x047e }
            r4.j(r0)     // Catch:{ Exception -> 0x047e }
            int r3 = r3 + 1
            r26 = r2
            goto L_0x0175
        L_0x0191:
            r12.close()     // Catch:{ Exception -> 0x047e }
            java.util.Date r0 = new java.util.Date     // Catch:{ Exception -> 0x047e }
            r0.<init>()     // Catch:{ Exception -> 0x047e }
            long r2 = r0.getTime()     // Catch:{ Exception -> 0x047e }
            long r4 = r23.getTime()     // Catch:{ Exception -> 0x047e }
            long r2 = r2 - r4
            java.util.concurrent.TimeUnit r0 = java.util.concurrent.TimeUnit.MILLISECONDS     // Catch:{ Exception -> 0x047e }
            long r2 = r0.toSeconds(r2)     // Catch:{ Exception -> 0x047e }
            java.lang.String r0 = "Zip Completed"
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x047e }
            r4.<init>()     // Catch:{ Exception -> 0x047e }
            java.lang.String r5 = "In "
            r4.append(r5)     // Catch:{ Exception -> 0x047e }
            r4.append(r2)     // Catch:{ Exception -> 0x047e }
            java.lang.String r2 = " Seconds"
            r4.append(r2)     // Catch:{ Exception -> 0x047e }
            java.lang.String r2 = r4.toString()     // Catch:{ Exception -> 0x047e }
            net.imedicaldoctor.imd.iMDLogger.f(r0, r2)     // Catch:{ Exception -> 0x047e }
            java.lang.String r0 = "0"
            return r0
        L_0x01c6:
            r39 = r3
            r17 = r26
            r26 = r28
            r3 = r36
            long r28 = r8.c()     // Catch:{ Exception -> 0x0430 }
            int r0 = (r28 > r20 ? 1 : (r28 == r20 ? 0 : -1))
            if (r0 <= 0) goto L_0x01f9
            r2 = 0
            java.lang.Object r0 = r6.get(r2)     // Catch:{ Exception -> 0x0430 }
            android.os.Bundle r0 = (android.os.Bundle) r0     // Catch:{ Exception -> 0x0430 }
            java.lang.String r0 = r0.getString(r14)     // Catch:{ Exception -> 0x0430 }
            if (r5 == 0) goto L_0x01e8
            net.imedicaldoctor.imd.Data.CompressHelper r2 = r3.f19847d     // Catch:{ Exception -> 0x0430 }
            r2.j(r0)     // Catch:{ Exception -> 0x0430 }
        L_0x01e8:
            r2 = 0
            r6.remove(r2)     // Catch:{ Exception -> 0x0430 }
            java.lang.Object r0 = r6.get(r2)     // Catch:{ Exception -> 0x0430 }
            android.os.Bundle r0 = (android.os.Bundle) r0     // Catch:{ Exception -> 0x0430 }
            r2 = r22
            long r20 = r0.getLong(r2)     // Catch:{ Exception -> 0x0430 }
            goto L_0x01fb
        L_0x01f9:
            r2 = r22
        L_0x01fb:
            boolean r0 = r4.t()     // Catch:{ Exception -> 0x0430 }
            if (r0 == 0) goto L_0x0216
            java.lang.String r0 = r4.k()     // Catch:{ Exception -> 0x0430 }
            r3.a(r0)     // Catch:{ Exception -> 0x0430 }
            r0 = r39
            r4 = r6
            r16 = r19
            r6 = r2
            r2 = r3
            r19 = r5
            r5 = r14
            r3 = r37
            goto L_0x041c
        L_0x0216:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0430 }
            r0.<init>()     // Catch:{ Exception -> 0x0430 }
            r22 = r2
            java.lang.String r2 = r3.f19845b     // Catch:{ Exception -> 0x0430 }
            r0.append(r2)     // Catch:{ Exception -> 0x0430 }
            r2 = r19
            r0.append(r2)     // Catch:{ Exception -> 0x0430 }
            r19 = r5
            java.lang.String r5 = r4.k()     // Catch:{ Exception -> 0x0430 }
            r0.append(r5)     // Catch:{ Exception -> 0x0430 }
            java.lang.String r5 = r0.toString()     // Catch:{ Exception -> 0x0430 }
            java.io.File r0 = new java.io.File     // Catch:{ Exception -> 0x042d }
            r0.<init>(r5)     // Catch:{ Exception -> 0x042d }
            boolean r0 = r0.exists()     // Catch:{ Exception -> 0x042d }
            if (r0 == 0) goto L_0x02a3
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x029f }
            r0.<init>()     // Catch:{ Exception -> 0x029f }
            r28 = r14
            java.lang.String r14 = r4.k()     // Catch:{ Exception -> 0x029f }
            r0.append(r14)     // Catch:{ Exception -> 0x029f }
            java.lang.String r14 = " Exists"
            r0.append(r14)     // Catch:{ Exception -> 0x029f }
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x029f }
            net.imedicaldoctor.imd.iMDLogger.f(r7, r0)     // Catch:{ Exception -> 0x029f }
            java.io.File r0 = new java.io.File     // Catch:{ Exception -> 0x029f }
            r0.<init>(r5)     // Catch:{ Exception -> 0x029f }
            long r29 = r0.length()     // Catch:{ Exception -> 0x029f }
            long r31 = r4.p()     // Catch:{ Exception -> 0x029f }
            int r0 = (r29 > r31 ? 1 : (r29 == r31 ? 0 : -1))
            if (r0 != 0) goto L_0x0281
            r0 = r39
            r4 = r1
            r1 = r3
            r18 = r5
            r7 = r17
            r5 = r19
            r13 = r22
            r15 = r23
            r14 = r28
            r3 = 0
            r19 = r2
            r2 = r37
            goto L_0x00d5
        L_0x0281:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x029f }
            r0.<init>()     // Catch:{ Exception -> 0x029f }
            java.lang.String r14 = r4.k()     // Catch:{ Exception -> 0x029f }
            r0.append(r14)     // Catch:{ Exception -> 0x029f }
            java.lang.String r14 = " Different Size. Deleteing old file"
            r0.append(r14)     // Catch:{ Exception -> 0x029f }
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x029f }
            net.imedicaldoctor.imd.iMDLogger.f(r7, r0)     // Catch:{ Exception -> 0x029f }
            net.imedicaldoctor.imd.Data.CompressHelper r0 = r3.f19847d     // Catch:{ Exception -> 0x029f }
            r0.j(r5)     // Catch:{ Exception -> 0x029f }
            goto L_0x02a5
        L_0x029f:
            r0 = move-exception
            r2 = r3
            goto L_0x0487
        L_0x02a3:
            r28 = r14
        L_0x02a5:
            r14 = 131072(0x20000, float:1.83671E-40)
            byte[] r0 = new byte[r14]     // Catch:{ Exception -> 0x042d }
            java.io.FileOutputStream r14 = new java.io.FileOutputStream     // Catch:{ Exception -> 0x03e9 }
            r18 = r5
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x03e2 }
            r5.<init>()     // Catch:{ Exception -> 0x03e2 }
            r29 = r6
            java.lang.String r6 = r3.f19845b     // Catch:{ Exception -> 0x03d5 }
            r5.append(r6)     // Catch:{ Exception -> 0x03d5 }
            r5.append(r2)     // Catch:{ Exception -> 0x03d5 }
            java.lang.String r6 = r4.k()     // Catch:{ Exception -> 0x03d5 }
            r5.append(r6)     // Catch:{ Exception -> 0x03d5 }
            java.lang.String r5 = r5.toString()     // Catch:{ Exception -> 0x03d5 }
            r14.<init>(r5)     // Catch:{ Exception -> 0x03d5 }
            java.io.BufferedOutputStream r5 = new java.io.BufferedOutputStream     // Catch:{ Exception -> 0x03d5 }
            r6 = 131072(0x20000, float:1.83671E-40)
            r5.<init>(r14, r6)     // Catch:{ Exception -> 0x03d5 }
            r14 = r39
        L_0x02d3:
            r16 = r2
            r30 = r4
            r2 = 0
            int r4 = r12.read(r0, r2, r6)     // Catch:{ Exception -> 0x03ca }
            r6 = -1
            if (r4 == r6) goto L_0x03b6
            r5.write(r0, r2, r4)     // Catch:{ Exception -> 0x03ca }
            java.util.Date r2 = new java.util.Date     // Catch:{ Exception -> 0x03ca }
            r2.<init>()     // Catch:{ Exception -> 0x03ca }
            long r32 = r2.getTime()     // Catch:{ Exception -> 0x03ca }
            long r34 = r14.getTime()     // Catch:{ Exception -> 0x03ca }
            r2 = r5
            long r4 = r32 - r34
            java.util.concurrent.TimeUnit r6 = java.util.concurrent.TimeUnit.MILLISECONDS     // Catch:{ Exception -> 0x03ca }
            long r4 = r6.toSeconds(r4)     // Catch:{ Exception -> 0x03ca }
            int r6 = (r4 > r26 ? 1 : (r4 == r26 ? 0 : -1))
            if (r6 <= 0) goto L_0x0340
            long r4 = r8.c()     // Catch:{ Exception -> 0x033b }
            double r4 = (double) r4     // Catch:{ Exception -> 0x033b }
            r39 = r2
            double r2 = (double) r9     // Catch:{ Exception -> 0x033b }
            double r4 = r4 / r2
            double r4 = r4 * r24
            r2 = 1
            java.lang.Object[] r3 = new java.lang.Object[r2]     // Catch:{ Exception -> 0x033b }
            java.lang.Double r4 = java.lang.Double.valueOf(r4)     // Catch:{ Exception -> 0x033b }
            r5 = 0
            r3[r5] = r4     // Catch:{ Exception -> 0x033b }
            java.lang.String r3 = java.lang.String.format(r15, r3)     // Catch:{ Exception -> 0x033b }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x033b }
            r4.<init>()     // Catch:{ Exception -> 0x033b }
            r4.append(r13)     // Catch:{ Exception -> 0x033b }
            r4.append(r3)     // Catch:{ Exception -> 0x033b }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x033b }
            net.imedicaldoctor.imd.iMDLogger.f(r7, r4)     // Catch:{ Exception -> 0x033b }
            r11.remove(r1)     // Catch:{ Exception -> 0x033b }
            r11.putString(r1, r3)     // Catch:{ Exception -> 0x033b }
            r3 = r37
            if (r3 == 0) goto L_0x0334
            r3.onNext(r11)     // Catch:{ Exception -> 0x03ac }
        L_0x0334:
            java.util.Date r4 = new java.util.Date     // Catch:{ Exception -> 0x03ac }
            r4.<init>()     // Catch:{ Exception -> 0x03ac }
            r14 = r4
            goto L_0x0345
        L_0x033b:
            r0 = move-exception
            r3 = r37
            goto L_0x03ad
        L_0x0340:
            r3 = r37
            r39 = r2
            r2 = 1
        L_0x0345:
            long r4 = r8.c()     // Catch:{ Exception -> 0x03ac }
            int r6 = (r4 > r20 ? 1 : (r4 == r20 ? 0 : -1))
            if (r6 <= 0) goto L_0x039e
            r4 = r29
            r5 = 0
            java.lang.Object r6 = r4.get(r5)     // Catch:{ Exception -> 0x0395 }
            android.os.Bundle r6 = (android.os.Bundle) r6     // Catch:{ Exception -> 0x0395 }
            r5 = r28
            java.lang.String r6 = r6.getString(r5)     // Catch:{ Exception -> 0x038e }
            if (r19 == 0) goto L_0x0368
            r2 = r36
            r28 = r0
            net.imedicaldoctor.imd.Data.CompressHelper r0 = r2.f19847d     // Catch:{ Exception -> 0x038c }
            r0.j(r6)     // Catch:{ Exception -> 0x038c }
            goto L_0x036c
        L_0x0368:
            r2 = r36
            r28 = r0
        L_0x036c:
            r6 = 0
            r4.remove(r6)     // Catch:{ Exception -> 0x038c }
            java.lang.Object r0 = r4.get(r6)     // Catch:{ Exception -> 0x038c }
            android.os.Bundle r0 = (android.os.Bundle) r0     // Catch:{ Exception -> 0x038c }
            r6 = r22
            long r20 = r0.getLong(r6)     // Catch:{ Exception -> 0x03c8 }
            r3 = r2
            r29 = r4
            r22 = r6
            r2 = r16
            r0 = r28
            r4 = r30
            r6 = 131072(0x20000, float:1.83671E-40)
            r28 = r5
            goto L_0x03a8
        L_0x038c:
            r0 = move-exception
            goto L_0x0391
        L_0x038e:
            r0 = move-exception
            r2 = r36
        L_0x0391:
            r6 = r22
            goto L_0x03fa
        L_0x0395:
            r0 = move-exception
            r2 = r36
            r6 = r22
            r5 = r28
            goto L_0x03fa
        L_0x039e:
            r5 = r28
            r3 = r36
            r2 = r16
            r4 = r30
            r6 = 131072(0x20000, float:1.83671E-40)
        L_0x03a8:
            r5 = r39
            goto L_0x02d3
        L_0x03ac:
            r0 = move-exception
        L_0x03ad:
            r2 = r36
            r6 = r22
            r5 = r28
            r4 = r29
            goto L_0x03fa
        L_0x03b6:
            r2 = r3
            r39 = r5
            r6 = r22
            r5 = r28
            r4 = r29
            r3 = r37
            r39.flush()     // Catch:{ Exception -> 0x03c8 }
            r39.close()     // Catch:{ Exception -> 0x03c8 }
            goto L_0x041b
        L_0x03c8:
            r0 = move-exception
            goto L_0x03fa
        L_0x03ca:
            r0 = move-exception
            r2 = r3
            r6 = r22
            r5 = r28
            r4 = r29
            r3 = r37
            goto L_0x03fa
        L_0x03d5:
            r0 = move-exception
            r16 = r2
            r2 = r3
            r30 = r4
            r6 = r22
            r5 = r28
            r4 = r29
            goto L_0x03f6
        L_0x03e2:
            r0 = move-exception
            r16 = r2
            r2 = r3
            r30 = r4
            goto L_0x03f1
        L_0x03e9:
            r0 = move-exception
            r16 = r2
            r2 = r3
            r30 = r4
            r18 = r5
        L_0x03f1:
            r4 = r6
            r6 = r22
            r5 = r28
        L_0x03f6:
            r3 = r37
            r14 = r39
        L_0x03fa:
            com.google.firebase.crashlytics.FirebaseCrashlytics r7 = com.google.firebase.crashlytics.FirebaseCrashlytics.d()     // Catch:{ Exception -> 0x047c }
            r7.g(r0)     // Catch:{ Exception -> 0x047c }
            java.lang.String r0 = "Decompress Error"
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x047c }
            r7.<init>()     // Catch:{ Exception -> 0x047c }
            java.lang.String r13 = "Can't write . "
            r7.append(r13)     // Catch:{ Exception -> 0x047c }
            java.lang.String r13 = r30.k()     // Catch:{ Exception -> 0x047c }
            r7.append(r13)     // Catch:{ Exception -> 0x047c }
            java.lang.String r7 = r7.toString()     // Catch:{ Exception -> 0x047c }
            net.imedicaldoctor.imd.iMDLogger.f(r0, r7)     // Catch:{ Exception -> 0x047c }
        L_0x041b:
            r0 = r14
        L_0x041c:
            r14 = r5
            r13 = r6
            r7 = r17
            r5 = r19
            r15 = r23
            r6 = r4
            r19 = r16
            r4 = r1
            r1 = r2
            r2 = r3
            r3 = 0
            goto L_0x00d5
        L_0x042d:
            r0 = move-exception
            r2 = r3
            goto L_0x0485
        L_0x0430:
            r0 = move-exception
            r2 = r3
            goto L_0x0480
        L_0x0433:
            r0 = move-exception
            r39 = r3
            r4 = r6
            r16 = r19
            r6 = r22
            r17 = r26
            r3 = r2
            r19 = r5
            r5 = r14
            r2 = r36
            r7 = r0
            com.google.firebase.crashlytics.FirebaseCrashlytics r0 = com.google.firebase.crashlytics.FirebaseCrashlytics.d()     // Catch:{ Exception -> 0x047c }
            r0.g(r7)     // Catch:{ Exception -> 0x047c }
            java.lang.String r0 = "Error"
            java.lang.StringBuilder r13 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x047c }
            r13.<init>()     // Catch:{ Exception -> 0x047c }
            java.lang.String r14 = "Error in getNextEntry : "
            r13.append(r14)     // Catch:{ Exception -> 0x047c }
            java.lang.String r14 = r7.getLocalizedMessage()     // Catch:{ Exception -> 0x047c }
            r13.append(r14)     // Catch:{ Exception -> 0x047c }
            java.lang.String r13 = r13.toString()     // Catch:{ Exception -> 0x047c }
            net.imedicaldoctor.imd.iMDLogger.f(r0, r13)     // Catch:{ Exception -> 0x047c }
            r7.printStackTrace()     // Catch:{ Exception -> 0x047c }
            java.lang.String r0 = r7.getLocalizedMessage()     // Catch:{ Exception -> 0x047c }
            java.lang.String r13 = "CRC mismatch"
            boolean r0 = r0.equals(r13)     // Catch:{ Exception -> 0x047c }
            if (r0 == 0) goto L_0x0477
            r0 = r39
            goto L_0x041c
        L_0x0477:
            java.lang.String r0 = r7.getLocalizedMessage()     // Catch:{ Exception -> 0x047c }
            return r0
        L_0x047c:
            r0 = move-exception
            goto L_0x0480
        L_0x047e:
            r0 = move-exception
            r2 = r1
        L_0x0480:
            r5 = r18
            goto L_0x0487
        L_0x0483:
            r0 = move-exception
            r2 = r1
        L_0x0485:
            r18 = r5
        L_0x0487:
            com.google.firebase.crashlytics.FirebaseCrashlytics r1 = com.google.firebase.crashlytics.FirebaseCrashlytics.d()
            r1.g(r0)
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r3 = r0.getLocalizedMessage()
            r1.append(r3)
            java.lang.String r3 = " in "
            r1.append(r3)
            r1.append(r5)
            java.lang.String r1 = r1.toString()
            java.lang.String r4 = "Error in unzip"
            net.imedicaldoctor.imd.iMDLogger.f(r4, r1)
            r0.printStackTrace()
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r0 = r0.getLocalizedMessage()
            r1.append(r0)
            r1.append(r3)
            r1.append(r5)
            java.lang.String r0 = r1.toString()
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: net.imedicaldoctor.imd.Decompress.g(io.reactivex.rxjava3.core.ObservableEmitter, java.lang.String, android.app.Activity):java.lang.String");
    }

    public boolean h() {
        try {
            ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(this.f19844a));
            while (true) {
                ZipEntry nextEntry = zipInputStream.getNextEntry();
                if (nextEntry != null) {
                    iMDLogger.j("Decompress", "Unzipping " + nextEntry.getName());
                    if (nextEntry.isDirectory()) {
                        a(nextEntry.getName());
                    } else {
                        FileOutputStream fileOutputStream = new FileOutputStream(this.f19845b + nextEntry.getName());
                        IOUtils.copyLarge((InputStream) zipInputStream, (OutputStream) fileOutputStream);
                        zipInputStream.closeEntry();
                        fileOutputStream.close();
                    }
                } else {
                    zipInputStream.close();
                    return true;
                }
            }
        } catch (Exception e2) {
            iMDLogger.f("Decompress", "unzip failed" + e2);
            return false;
        }
    }

    public boolean i() {
        try {
            FileInputStream fileInputStream = new FileInputStream(this.f19844a);
            ZipInputStream zipInputStream = new ZipInputStream(new BufferedInputStream(fileInputStream));
            long length = new File(this.f19844a).length();
            long j = 0;
            while (true) {
                ZipEntry nextEntry = zipInputStream.getNextEntry();
                if (nextEntry != null) {
                    j += nextEntry.getCompressedSize();
                    long j2 = j / length;
                    if (nextEntry.isDirectory()) {
                        a(nextEntry.getName());
                    } else {
                        String str = this.f19845b + "/" + nextEntry.getName();
                        if (new File(str).exists()) {
                            this.f19847d.j(str);
                        }
                        byte[] bArr = new byte[262144];
                        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(this.f19845b + "/" + nextEntry.getName()), 262144);
                        while (true) {
                            int read = zipInputStream.read(bArr, 0, 262144);
                            if (read == -1) {
                                break;
                            }
                            bufferedOutputStream.write(bArr, 0, read);
                        }
                        bufferedOutputStream.flush();
                        bufferedOutputStream.close();
                    }
                } else {
                    zipInputStream.close();
                    fileInputStream.close();
                    return true;
                }
            }
        } catch (Exception e2) {
            FirebaseCrashlytics.d().g(e2);
            iMDLogger.f("Error in unzip", e2.getLocalizedMessage());
            return false;
        }
    }

    public Observable<String> j() {
        return Observable.x1(new ObservableOnSubscribe<String>() {
            public void a(@NonNull ObservableEmitter<String> observableEmitter) throws Throwable {
                if (Boolean.valueOf(Decompress.this.i()).booleanValue()) {
                    observableEmitter.onComplete();
                } else {
                    observableEmitter.onError((Throwable) null);
                }
            }
        });
    }

    public String k(ObservableEmitter<Bundle> observableEmitter, String str) {
        String str2;
        long j;
        Date date;
        String str3;
        String str4 = "progress";
        String str5 = "";
        try {
            FileInputStream fileInputStream = new FileInputStream(this.f19844a);
            ZipInputStream zipInputStream = new ZipInputStream(new BufferedInputStream(fileInputStream));
            long length = new File(this.f19844a).length();
            Date date2 = new Date();
            Bundle bundle = new Bundle();
            bundle.putString(str4, str5);
            bundle.putString("labelText", str);
            File file = new File(this.f19845b);
            long j2 = 0;
            String str6 = str5;
            Date date3 = date2;
            while (true) {
                try {
                    ZipEntry nextEntry = zipInputStream.getNextEntry();
                    if (nextEntry == null) {
                        try {
                            zipInputStream.close();
                            fileInputStream.close();
                            return "0";
                        } catch (Exception e2) {
                            e = e2;
                            str5 = str6;
                        }
                    } else {
                        try {
                            j2 += nextEntry.getCompressedSize();
                            if (nextEntry.isDirectory()) {
                                a(nextEntry.getName());
                            } else {
                                Date date4 = date3;
                                if (TimeUnit.MILLISECONDS.toSeconds(new Date().getTime() - date3.getTime()) > 1) {
                                    str2 = str6;
                                    j = j2;
                                    try {
                                        String format = String.format("%.2f", new Object[]{Double.valueOf((((double) j2) / ((double) length)) * 100.0d)});
                                        iMDLogger.f("Decompress", "Percent : " + format);
                                        bundle.remove(str4);
                                        bundle.putString(str4, format);
                                        observableEmitter.onNext(bundle);
                                        date = new Date();
                                    } catch (Exception e3) {
                                        e = e3;
                                        str5 = str2;
                                        FirebaseCrashlytics.d().g(e);
                                        iMDLogger.f("Error in unzip", e.getLocalizedMessage() + " in " + str5);
                                        e.printStackTrace();
                                        return e.getLocalizedMessage() + " in " + str5;
                                    }
                                } else {
                                    String str7 = str6;
                                    j = j2;
                                    ObservableEmitter<Bundle> observableEmitter2 = observableEmitter;
                                    date = date4;
                                }
                                String str8 = this.f19845b + "/" + nextEntry.getName();
                                try {
                                    if (new File(str8).exists()) {
                                        iMDLogger.f("Decompress", nextEntry.getName() + " Exists.");
                                        if (new File(str8).length() == nextEntry.getSize()) {
                                            str3 = str4;
                                            date3 = date;
                                            str6 = str8;
                                            str4 = str3;
                                            j2 = j;
                                        } else {
                                            iMDLogger.f("Decompress", nextEntry.getName() + " Different Size.");
                                            this.f19847d.j(str8);
                                            iMDLogger.f("Decompress", nextEntry.getName() + " Deleted.");
                                        }
                                    }
                                    if (nextEntry.getSize() > file.getUsableSpace()) {
                                        iMDLogger.f("Decompress", "Not Enough space");
                                        return ExifInterface.S4;
                                    }
                                    byte[] bArr = new byte[262144];
                                    StringBuilder sb = new StringBuilder();
                                    str3 = str4;
                                    sb.append(this.f19845b);
                                    sb.append("/");
                                    sb.append(nextEntry.getName());
                                    int i = 262144;
                                    BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(sb.toString()), 262144);
                                    while (true) {
                                        int read = zipInputStream.read(bArr, 0, i);
                                        if (read == -1) {
                                            break;
                                        }
                                        bufferedOutputStream.write(bArr, 0, read);
                                        i = 262144;
                                    }
                                    bufferedOutputStream.flush();
                                    bufferedOutputStream.close();
                                    date3 = date;
                                    str6 = str8;
                                    str4 = str3;
                                    j2 = j;
                                } catch (Exception e4) {
                                    e = e4;
                                    str5 = str8;
                                    FirebaseCrashlytics.d().g(e);
                                    iMDLogger.f("Error in unzip", e.getLocalizedMessage() + " in " + str5);
                                    e.printStackTrace();
                                    return e.getLocalizedMessage() + " in " + str5;
                                }
                            }
                        } catch (Exception e5) {
                            e = e5;
                            str2 = str6;
                            str5 = str2;
                            FirebaseCrashlytics.d().g(e);
                            iMDLogger.f("Error in unzip", e.getLocalizedMessage() + " in " + str5);
                            e.printStackTrace();
                            return e.getLocalizedMessage() + " in " + str5;
                        }
                    }
                } catch (Exception e6) {
                    String str9 = str4;
                    Date date5 = date3;
                    str2 = str6;
                    ObservableEmitter<Bundle> observableEmitter3 = observableEmitter;
                    Exception exc = e6;
                    FirebaseCrashlytics.d().g(exc);
                    iMDLogger.f("Error", "Error in getNextEntry : " + exc.getLocalizedMessage());
                    exc.printStackTrace();
                    if (!exc.getLocalizedMessage().equals("CRC mismatch")) {
                        return exc.getLocalizedMessage();
                    }
                    date3 = date5;
                    str6 = str2;
                    str4 = str9;
                }
            }
        } catch (Exception e7) {
            e = e7;
            FirebaseCrashlytics.d().g(e);
            iMDLogger.f("Error in unzip", e.getLocalizedMessage() + " in " + str5);
            e.printStackTrace();
            return e.getLocalizedMessage() + " in " + str5;
        }
    }

    public String l(ObservableEmitter<Bundle> observableEmitter, String str) {
        ZipInputStream zipInputStream;
        Date date;
        ZipInputStream zipInputStream2;
        Decompress decompress = this;
        String str2 = "progress";
        String str3 = "";
        try {
            FileInputStream fileInputStream = new FileInputStream(decompress.f19844a);
            ZipInputStream zipInputStream3 = new ZipInputStream(new BufferedInputStream(fileInputStream));
            long length = new File(decompress.f19844a).length();
            Date date2 = new Date();
            Bundle bundle = new Bundle();
            bundle.putString(str2, str3);
            bundle.putString("labelText", str);
            File file = new File(decompress.f19845b);
            long j = 0;
            String str4 = str3;
            Date date3 = date2;
            while (true) {
                try {
                    ZipEntry nextEntry = zipInputStream3.getNextEntry();
                    if (nextEntry == null) {
                        try {
                            zipInputStream3.close();
                            fileInputStream.close();
                            return "0";
                        } catch (Exception e2) {
                            e = e2;
                            str3 = str4;
                            FirebaseCrashlytics.d().g(e);
                            iMDLogger.f("Error in unzip", e.getLocalizedMessage() + " in " + str3);
                            e.printStackTrace();
                            return e.getLocalizedMessage() + " in " + str3;
                        }
                    } else {
                        j += nextEntry.getCompressedSize();
                        if (nextEntry.isDirectory()) {
                            decompress.a(nextEntry.getName());
                        } else {
                            Date date4 = date3;
                            FileInputStream fileInputStream2 = fileInputStream;
                            if (TimeUnit.MILLISECONDS.toSeconds(new Date().getTime() - date3.getTime()) > 1) {
                                zipInputStream = zipInputStream3;
                                String format = String.format("%.2f", new Object[]{Double.valueOf((((double) j) / ((double) length)) * 100.0d)});
                                iMDLogger.f("Decompress", "Percent : " + format);
                                bundle.remove(str2);
                                bundle.putString(str2, format);
                                observableEmitter.onNext(bundle);
                                date = new Date();
                            } else {
                                ObservableEmitter<Bundle> observableEmitter2 = observableEmitter;
                                zipInputStream = zipInputStream3;
                                date = date4;
                            }
                            str4 = decompress.f19845b + "/" + nextEntry.getName();
                            if (new File(str4).exists()) {
                                decompress.f19847d.j(str4);
                            }
                            if (nextEntry.getSize() > file.getUsableSpace()) {
                                iMDLogger.f("Decompress", "Not Enough space");
                                return ExifInterface.S4;
                            }
                            byte[] bArr = new byte[262144];
                            StringBuilder sb = new StringBuilder();
                            String str5 = str2;
                            sb.append(decompress.f19845b);
                            sb.append("/");
                            sb.append(nextEntry.getName());
                            int i = 262144;
                            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(sb.toString()), 262144);
                            while (true) {
                                zipInputStream2 = zipInputStream;
                                int read = zipInputStream2.read(bArr, 0, i);
                                if (read == -1) {
                                    break;
                                }
                                bufferedOutputStream.write(bArr, 0, read);
                                zipInputStream = zipInputStream2;
                                i = 262144;
                            }
                            bufferedOutputStream.flush();
                            bufferedOutputStream.close();
                            decompress = this;
                            fileInputStream = fileInputStream2;
                            str2 = str5;
                            Date date5 = date;
                            zipInputStream3 = zipInputStream2;
                            date3 = date5;
                        }
                    }
                } catch (Exception e3) {
                    String str6 = str2;
                    Date date6 = date3;
                    FileInputStream fileInputStream3 = fileInputStream;
                    ZipInputStream zipInputStream4 = zipInputStream3;
                    ObservableEmitter<Bundle> observableEmitter3 = observableEmitter;
                    Exception exc = e3;
                    FirebaseCrashlytics.d().g(exc);
                    iMDLogger.f("Error", "Error in getNextEntry : " + exc.getLocalizedMessage());
                    exc.printStackTrace();
                    if (!exc.getLocalizedMessage().equals("CRC mismatch")) {
                        return exc.getLocalizedMessage();
                    }
                    decompress = this;
                    zipInputStream3 = zipInputStream4;
                    fileInputStream = fileInputStream3;
                    str2 = str6;
                    date3 = date6;
                }
            }
        } catch (Exception e4) {
            e = e4;
            FirebaseCrashlytics.d().g(e);
            iMDLogger.f("Error in unzip", e.getLocalizedMessage() + " in " + str3);
            e.printStackTrace();
            return e.getLocalizedMessage() + " in " + str3;
        }
    }

    /* JADX WARNING: type inference failed for: r12v18 */
    /* JADX WARNING: type inference failed for: r12v21 */
    /* JADX WARNING: type inference failed for: r12v35 */
    /* JADX WARNING: Code restructure failed: missing block: B:111:0x036f, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:112:0x0370, code lost:
        r5 = r20;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x00c3, code lost:
        if (r5 == false) goto L_0x00dd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x00c9, code lost:
        if (r2 >= r6.size()) goto L_0x00f5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x00cb, code lost:
        r1.f19847d.j(((android.os.Bundle) r6.get(r2)).getString("filePath"));
        r2 = r2 + 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x00e1, code lost:
        if (r2 >= r7.size()) goto L_0x00f5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:0x00e3, code lost:
        r1.f19847d.j(((android.os.Bundle) r7.get(r2)).getString("filePath"));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x00f2, code lost:
        r2 = r2 + 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:28:?, code lost:
        r0 = r8.iterator();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:0x00fd, code lost:
        if (r0.hasNext() == false) goto L_0x0109;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:0x00ff, code lost:
        ((java.io.FileInputStream) r0.next()).close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:0x0109, code lost:
        r3.close();
        r15.close();
        r12.close();
        r9.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:34:?, code lost:
        net.imedicaldoctor.imd.iMDLogger.f("Zip Completed", "In " + java.util.concurrent.TimeUnit.MILLISECONDS.toSeconds(new java.util.Date().getTime() - r18.getTime()) + " Seconds");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:0x0146, code lost:
        return "0";
     */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [] */
    /* JADX WARNING: Missing exception handler attribute for start block: B:33:0x0115 */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.lang.String m(io.reactivex.rxjava3.core.ObservableEmitter<android.os.Bundle> r31, java.lang.String r32, android.app.Activity r33) {
        /*
            r30 = this;
            r1 = r30
            r2 = r31
            java.lang.String r3 = "/"
            java.lang.String r4 = "progress"
            java.lang.String r5 = ""
            java.util.ArrayList r6 = new java.util.ArrayList     // Catch:{ Exception -> 0x0373 }
            r6.<init>()     // Catch:{ Exception -> 0x0373 }
            java.util.ArrayList r7 = new java.util.ArrayList     // Catch:{ Exception -> 0x0373 }
            r7.<init>()     // Catch:{ Exception -> 0x0373 }
            java.util.Vector r8 = new java.util.Vector     // Catch:{ Exception -> 0x0373 }
            r0 = 10
            r8.<init>(r0)     // Catch:{ Exception -> 0x0373 }
            r10 = 0
            r0 = 1
        L_0x001e:
            r12 = 11
            java.lang.String r13 = "offset"
            java.lang.String r14 = "filePath"
            if (r0 >= r12) goto L_0x0066
            java.lang.StringBuilder r12 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0063 }
            r12.<init>()     // Catch:{ Exception -> 0x0063 }
            java.lang.String r15 = r1.f19844a     // Catch:{ Exception -> 0x0063 }
            r12.append(r15)     // Catch:{ Exception -> 0x0063 }
            java.lang.String r15 = "."
            r12.append(r15)     // Catch:{ Exception -> 0x0063 }
            r12.append(r0)     // Catch:{ Exception -> 0x0063 }
            java.lang.String r12 = r12.toString()     // Catch:{ Exception -> 0x0063 }
            android.os.Bundle r15 = new android.os.Bundle     // Catch:{ Exception -> 0x0063 }
            r15.<init>()     // Catch:{ Exception -> 0x0063 }
            java.io.File r9 = new java.io.File     // Catch:{ Exception -> 0x0063 }
            r9.<init>(r12)     // Catch:{ Exception -> 0x0063 }
            long r16 = r9.length()     // Catch:{ Exception -> 0x0063 }
            long r10 = r10 + r16
            r15.putString(r14, r12)     // Catch:{ Exception -> 0x0063 }
            r15.putLong(r13, r10)     // Catch:{ Exception -> 0x0063 }
            r6.add(r15)     // Catch:{ Exception -> 0x0063 }
            r7.add(r15)     // Catch:{ Exception -> 0x0063 }
            java.io.FileInputStream r9 = new java.io.FileInputStream     // Catch:{ Exception -> 0x0063 }
            r9.<init>(r12)     // Catch:{ Exception -> 0x0063 }
            r8.add(r9)     // Catch:{ Exception -> 0x0063 }
            int r0 = r0 + 1
            goto L_0x001e
        L_0x0063:
            r0 = move-exception
            goto L_0x0376
        L_0x0066:
            java.util.Enumeration r0 = r8.elements()     // Catch:{ Exception -> 0x0373 }
            java.io.SequenceInputStream r9 = new java.io.SequenceInputStream     // Catch:{ Exception -> 0x0373 }
            r9.<init>(r0)     // Catch:{ Exception -> 0x0373 }
            java.io.BufferedInputStream r12 = new java.io.BufferedInputStream     // Catch:{ Exception -> 0x0373 }
            r15 = 131072(0x20000, float:1.83671E-40)
            r12.<init>(r9, r15)     // Catch:{ Exception -> 0x0373 }
            com.google.common.io.CountingInputStream r15 = new com.google.common.io.CountingInputStream     // Catch:{ Exception -> 0x0373 }
            r15.<init>(r12)     // Catch:{ Exception -> 0x0373 }
            r17 = r3
            java.util.zip.ZipInputStream r3 = new java.util.zip.ZipInputStream     // Catch:{ Exception -> 0x0373 }
            r3.<init>(r15)     // Catch:{ Exception -> 0x0373 }
            java.util.Date r0 = new java.util.Date     // Catch:{ Exception -> 0x0373 }
            r0.<init>()     // Catch:{ Exception -> 0x0373 }
            java.util.Date r18 = new java.util.Date     // Catch:{ Exception -> 0x0373 }
            r18.<init>()     // Catch:{ Exception -> 0x0373 }
            android.os.Bundle r2 = new android.os.Bundle     // Catch:{ Exception -> 0x0373 }
            r2.<init>()     // Catch:{ Exception -> 0x0373 }
            r2.putString(r4, r5)     // Catch:{ Exception -> 0x0373 }
            r19 = r0
            java.lang.String r0 = "labelText"
            r20 = r5
            r5 = r32
            r2.putString(r0, r5)     // Catch:{ Exception -> 0x036f }
            java.io.File r0 = new java.io.File     // Catch:{ Exception -> 0x036f }
            java.lang.String r5 = r1.f19845b     // Catch:{ Exception -> 0x036f }
            r0.<init>(r5)     // Catch:{ Exception -> 0x036f }
            android.content.SharedPreferences r0 = android.preference.PreferenceManager.getDefaultSharedPreferences(r33)     // Catch:{ Exception -> 0x036f }
            java.lang.String r5 = "lessspace"
            r21 = r2
            r2 = 0
            boolean r5 = r0.getBoolean(r5, r2)     // Catch:{ Exception -> 0x036f }
            java.lang.Object r0 = r6.get(r2)     // Catch:{ Exception -> 0x036f }
            android.os.Bundle r0 = (android.os.Bundle) r0     // Catch:{ Exception -> 0x036f }
            long r22 = r0.getLong(r13)     // Catch:{ Exception -> 0x036f }
        L_0x00bd:
            java.util.zip.ZipEntry r24 = r3.getNextEntry()     // Catch:{ Exception -> 0x032c }
            if (r24 != 0) goto L_0x0147
            if (r5 == 0) goto L_0x00dd
        L_0x00c5:
            int r0 = r6.size()     // Catch:{ Exception -> 0x036f }
            if (r2 >= r0) goto L_0x00f5
            java.lang.Object r0 = r6.get(r2)     // Catch:{ Exception -> 0x036f }
            android.os.Bundle r0 = (android.os.Bundle) r0     // Catch:{ Exception -> 0x036f }
            java.lang.String r0 = r0.getString(r14)     // Catch:{ Exception -> 0x036f }
            net.imedicaldoctor.imd.Data.CompressHelper r4 = r1.f19847d     // Catch:{ Exception -> 0x036f }
            r4.j(r0)     // Catch:{ Exception -> 0x036f }
            int r2 = r2 + 1
            goto L_0x00c5
        L_0x00dd:
            int r0 = r7.size()     // Catch:{ Exception -> 0x036f }
            if (r2 >= r0) goto L_0x00f5
            java.lang.Object r0 = r7.get(r2)     // Catch:{ Exception -> 0x036f }
            android.os.Bundle r0 = (android.os.Bundle) r0     // Catch:{ Exception -> 0x036f }
            java.lang.String r0 = r0.getString(r14)     // Catch:{ Exception -> 0x036f }
            net.imedicaldoctor.imd.Data.CompressHelper r4 = r1.f19847d     // Catch:{ Exception -> 0x036f }
            r4.j(r0)     // Catch:{ Exception -> 0x036f }
            int r2 = r2 + 1
            goto L_0x00dd
        L_0x00f5:
            java.util.Iterator r0 = r8.iterator()     // Catch:{ Exception -> 0x0115 }
        L_0x00f9:
            boolean r2 = r0.hasNext()     // Catch:{ Exception -> 0x0115 }
            if (r2 == 0) goto L_0x0109
            java.lang.Object r2 = r0.next()     // Catch:{ Exception -> 0x0115 }
            java.io.FileInputStream r2 = (java.io.FileInputStream) r2     // Catch:{ Exception -> 0x0115 }
            r2.close()     // Catch:{ Exception -> 0x0115 }
            goto L_0x00f9
        L_0x0109:
            r3.close()     // Catch:{ Exception -> 0x0115 }
            r15.close()     // Catch:{ Exception -> 0x0115 }
            r12.close()     // Catch:{ Exception -> 0x0115 }
            r9.close()     // Catch:{ Exception -> 0x0115 }
        L_0x0115:
            java.util.Date r0 = new java.util.Date     // Catch:{ Exception -> 0x036f }
            r0.<init>()     // Catch:{ Exception -> 0x036f }
            long r2 = r0.getTime()     // Catch:{ Exception -> 0x036f }
            long r4 = r18.getTime()     // Catch:{ Exception -> 0x036f }
            long r2 = r2 - r4
            java.util.concurrent.TimeUnit r0 = java.util.concurrent.TimeUnit.MILLISECONDS     // Catch:{ Exception -> 0x036f }
            long r2 = r0.toSeconds(r2)     // Catch:{ Exception -> 0x036f }
            java.lang.String r0 = "Zip Completed"
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x036f }
            r4.<init>()     // Catch:{ Exception -> 0x036f }
            java.lang.String r5 = "In "
            r4.append(r5)     // Catch:{ Exception -> 0x036f }
            r4.append(r2)     // Catch:{ Exception -> 0x036f }
            java.lang.String r2 = " Seconds"
            r4.append(r2)     // Catch:{ Exception -> 0x036f }
            java.lang.String r2 = r4.toString()     // Catch:{ Exception -> 0x036f }
            net.imedicaldoctor.imd.iMDLogger.f(r0, r2)     // Catch:{ Exception -> 0x036f }
            java.lang.String r0 = "0"
            return r0
        L_0x0147:
            long r25 = r15.c()     // Catch:{ Exception -> 0x036f }
            int r0 = (r25 > r22 ? 1 : (r25 == r22 ? 0 : -1))
            if (r0 <= 0) goto L_0x016e
            java.lang.Object r0 = r6.get(r2)     // Catch:{ Exception -> 0x036f }
            android.os.Bundle r0 = (android.os.Bundle) r0     // Catch:{ Exception -> 0x036f }
            java.lang.String r0 = r0.getString(r14)     // Catch:{ Exception -> 0x036f }
            if (r5 == 0) goto L_0x0161
            net.imedicaldoctor.imd.Data.CompressHelper r2 = r1.f19847d     // Catch:{ Exception -> 0x036f }
            r2.j(r0)     // Catch:{ Exception -> 0x036f }
            r2 = 0
        L_0x0161:
            r6.remove(r2)     // Catch:{ Exception -> 0x036f }
            java.lang.Object r0 = r6.get(r2)     // Catch:{ Exception -> 0x036f }
            android.os.Bundle r0 = (android.os.Bundle) r0     // Catch:{ Exception -> 0x036f }
            long r22 = r0.getLong(r13)     // Catch:{ Exception -> 0x036f }
        L_0x016e:
            boolean r0 = r24.isDirectory()     // Catch:{ Exception -> 0x036f }
            if (r0 == 0) goto L_0x0189
            java.lang.String r0 = r24.getName()     // Catch:{ Exception -> 0x036f }
            r1.a(r0)     // Catch:{ Exception -> 0x036f }
            r29 = r7
            r33 = r8
            r26 = r12
            r12 = r13
            r13 = r17
            r2 = 0
            r17 = r4
            goto L_0x031c
        L_0x0189:
            java.util.Date r0 = new java.util.Date     // Catch:{ Exception -> 0x036f }
            r0.<init>()     // Catch:{ Exception -> 0x036f }
            long r25 = r0.getTime()     // Catch:{ Exception -> 0x036f }
            long r27 = r19.getTime()     // Catch:{ Exception -> 0x036f }
            r29 = r7
            r2 = r8
            long r7 = r25 - r27
            java.util.concurrent.TimeUnit r0 = java.util.concurrent.TimeUnit.MILLISECONDS     // Catch:{ Exception -> 0x036f }
            long r7 = r0.toSeconds(r7)     // Catch:{ Exception -> 0x036f }
            r33 = r2
            r26 = r12
            r25 = r13
            r2 = 1
            long r12 = (long) r2
            java.lang.String r0 = "Decompress"
            int r2 = (r7 > r12 ? 1 : (r7 == r12 ? 0 : -1))
            if (r2 <= 0) goto L_0x01f5
            long r7 = r15.c()     // Catch:{ Exception -> 0x036f }
            double r7 = (double) r7     // Catch:{ Exception -> 0x036f }
            double r12 = (double) r10     // Catch:{ Exception -> 0x036f }
            double r7 = r7 / r12
            r12 = 4636737291354636288(0x4059000000000000, double:100.0)
            double r7 = r7 * r12
            java.lang.String r2 = "%.2f"
            r12 = 1
            java.lang.Object[] r13 = new java.lang.Object[r12]     // Catch:{ Exception -> 0x036f }
            java.lang.Double r7 = java.lang.Double.valueOf(r7)     // Catch:{ Exception -> 0x036f }
            r8 = 0
            r13[r8] = r7     // Catch:{ Exception -> 0x036f }
            java.lang.String r2 = java.lang.String.format(r2, r13)     // Catch:{ Exception -> 0x036f }
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x036f }
            r7.<init>()     // Catch:{ Exception -> 0x036f }
            java.lang.String r8 = "Percent : "
            r7.append(r8)     // Catch:{ Exception -> 0x036f }
            r7.append(r2)     // Catch:{ Exception -> 0x036f }
            java.lang.String r7 = r7.toString()     // Catch:{ Exception -> 0x036f }
            net.imedicaldoctor.imd.iMDLogger.f(r0, r7)     // Catch:{ Exception -> 0x036f }
            r7 = r21
            r7.remove(r4)     // Catch:{ Exception -> 0x036f }
            r7.putString(r4, r2)     // Catch:{ Exception -> 0x036f }
            r2 = r31
            if (r2 == 0) goto L_0x01ed
            r2.onNext(r7)     // Catch:{ Exception -> 0x036f }
        L_0x01ed:
            java.util.Date r8 = new java.util.Date     // Catch:{ Exception -> 0x036f }
            r8.<init>()     // Catch:{ Exception -> 0x036f }
            r19 = r8
            goto L_0x01fa
        L_0x01f5:
            r2 = r31
            r7 = r21
            r12 = 1
        L_0x01fa:
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x036f }
            r8.<init>()     // Catch:{ Exception -> 0x036f }
            java.lang.String r13 = r1.f19845b     // Catch:{ Exception -> 0x036f }
            r8.append(r13)     // Catch:{ Exception -> 0x036f }
            r13 = r17
            r8.append(r13)     // Catch:{ Exception -> 0x036f }
            java.lang.String r12 = r24.getName()     // Catch:{ Exception -> 0x036f }
            r8.append(r12)     // Catch:{ Exception -> 0x036f }
            java.lang.String r8 = r8.toString()     // Catch:{ Exception -> 0x036f }
            java.io.File r12 = new java.io.File     // Catch:{ Exception -> 0x0329 }
            r12.<init>(r8)     // Catch:{ Exception -> 0x0329 }
            boolean r12 = r12.exists()     // Catch:{ Exception -> 0x0329 }
            if (r12 == 0) goto L_0x0276
            java.lang.StringBuilder r12 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0329 }
            r12.<init>()     // Catch:{ Exception -> 0x0329 }
            java.lang.String r2 = r24.getName()     // Catch:{ Exception -> 0x0329 }
            r12.append(r2)     // Catch:{ Exception -> 0x0329 }
            java.lang.String r2 = " Exists"
            r12.append(r2)     // Catch:{ Exception -> 0x0329 }
            java.lang.String r2 = r12.toString()     // Catch:{ Exception -> 0x0329 }
            net.imedicaldoctor.imd.iMDLogger.f(r0, r2)     // Catch:{ Exception -> 0x0329 }
            java.io.File r2 = new java.io.File     // Catch:{ Exception -> 0x0329 }
            r2.<init>(r8)     // Catch:{ Exception -> 0x0329 }
            long r20 = r2.length()     // Catch:{ Exception -> 0x0329 }
            long r27 = r24.getSize()     // Catch:{ Exception -> 0x0329 }
            int r2 = (r20 > r27 ? 1 : (r20 == r27 ? 0 : -1))
            if (r2 != 0) goto L_0x0259
            r21 = r7
            r20 = r8
            r17 = r13
            r13 = r25
            r12 = r26
            r7 = r29
            r2 = 0
            r8 = r33
            goto L_0x00bd
        L_0x0259:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0329 }
            r2.<init>()     // Catch:{ Exception -> 0x0329 }
            java.lang.String r12 = r24.getName()     // Catch:{ Exception -> 0x0329 }
            r2.append(r12)     // Catch:{ Exception -> 0x0329 }
            java.lang.String r12 = " Different Size. Deleteing old file"
            r2.append(r12)     // Catch:{ Exception -> 0x0329 }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x0329 }
            net.imedicaldoctor.imd.iMDLogger.f(r0, r2)     // Catch:{ Exception -> 0x0329 }
            net.imedicaldoctor.imd.Data.CompressHelper r0 = r1.f19847d     // Catch:{ Exception -> 0x0329 }
            r0.j(r8)     // Catch:{ Exception -> 0x0329 }
        L_0x0276:
            r2 = 131072(0x20000, float:1.83671E-40)
            byte[] r0 = new byte[r2]     // Catch:{ Exception -> 0x0329 }
            java.io.FileOutputStream r2 = new java.io.FileOutputStream     // Catch:{ Exception -> 0x02f1 }
            java.lang.StringBuilder r12 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x02f1 }
            r12.<init>()     // Catch:{ Exception -> 0x02f1 }
            r17 = r4
            java.lang.String r4 = r1.f19845b     // Catch:{ Exception -> 0x02ef }
            r12.append(r4)     // Catch:{ Exception -> 0x02ef }
            r12.append(r13)     // Catch:{ Exception -> 0x02ef }
            java.lang.String r4 = r24.getName()     // Catch:{ Exception -> 0x02ef }
            r12.append(r4)     // Catch:{ Exception -> 0x02ef }
            java.lang.String r4 = r12.toString()     // Catch:{ Exception -> 0x02ef }
            r2.<init>(r4)     // Catch:{ Exception -> 0x02ef }
            java.io.BufferedOutputStream r4 = new java.io.BufferedOutputStream     // Catch:{ Exception -> 0x02ef }
            r12 = 131072(0x20000, float:1.83671E-40)
            r4.<init>(r2, r12)     // Catch:{ Exception -> 0x02ef }
        L_0x02a0:
            r21 = r7
            r2 = 0
            int r7 = r3.read(r0, r2, r12)     // Catch:{ Exception -> 0x02eb }
            r12 = -1
            if (r7 == r12) goto L_0x02e0
            r4.write(r0, r2, r7)     // Catch:{ Exception -> 0x02eb }
            long r27 = r15.c()     // Catch:{ Exception -> 0x02eb }
            int r7 = (r27 > r22 ? 1 : (r27 == r22 ? 0 : -1))
            if (r7 <= 0) goto L_0x02db
            java.lang.Object r7 = r6.get(r2)     // Catch:{ Exception -> 0x02eb }
            android.os.Bundle r7 = (android.os.Bundle) r7     // Catch:{ Exception -> 0x02d9 }
            java.lang.String r2 = r7.getString(r14)     // Catch:{ Exception -> 0x02d9 }
            if (r5 == 0) goto L_0x02c6
            net.imedicaldoctor.imd.Data.CompressHelper r7 = r1.f19847d     // Catch:{ Exception -> 0x02d9 }
            r7.j(r2)     // Catch:{ Exception -> 0x02d9 }
        L_0x02c6:
            r2 = 0
            r6.remove(r2)     // Catch:{ Exception -> 0x02eb }
            java.lang.Object r7 = r6.get(r2)     // Catch:{ Exception -> 0x02eb }
            android.os.Bundle r7 = (android.os.Bundle) r7     // Catch:{ Exception -> 0x02eb }
            r12 = r25
            long r22 = r7.getLong(r12)     // Catch:{ Exception -> 0x02e9 }
            r25 = r12
            goto L_0x02db
        L_0x02d9:
            r0 = move-exception
            goto L_0x02f6
        L_0x02db:
            r7 = r21
            r12 = 131072(0x20000, float:1.83671E-40)
            goto L_0x02a0
        L_0x02e0:
            r12 = r25
            r4.flush()     // Catch:{ Exception -> 0x02e9 }
            r4.close()     // Catch:{ Exception -> 0x02e9 }
            goto L_0x031a
        L_0x02e9:
            r0 = move-exception
            goto L_0x02f9
        L_0x02eb:
            r0 = move-exception
            r12 = r25
            goto L_0x02f9
        L_0x02ef:
            r0 = move-exception
            goto L_0x02f4
        L_0x02f1:
            r0 = move-exception
            r17 = r4
        L_0x02f4:
            r21 = r7
        L_0x02f6:
            r12 = r25
            r2 = 0
        L_0x02f9:
            com.google.firebase.crashlytics.FirebaseCrashlytics r4 = com.google.firebase.crashlytics.FirebaseCrashlytics.d()     // Catch:{ Exception -> 0x0329 }
            r4.g(r0)     // Catch:{ Exception -> 0x0329 }
            java.lang.String r0 = "Decompress Error"
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0329 }
            r4.<init>()     // Catch:{ Exception -> 0x0329 }
            java.lang.String r7 = "Can't write . "
            r4.append(r7)     // Catch:{ Exception -> 0x0329 }
            java.lang.String r7 = r24.getName()     // Catch:{ Exception -> 0x0329 }
            r4.append(r7)     // Catch:{ Exception -> 0x0329 }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x0329 }
            net.imedicaldoctor.imd.iMDLogger.f(r0, r4)     // Catch:{ Exception -> 0x0329 }
        L_0x031a:
            r20 = r8
        L_0x031c:
            r8 = r33
            r4 = r17
            r7 = r29
            r17 = r13
            r13 = r12
            r12 = r26
            goto L_0x00bd
        L_0x0329:
            r0 = move-exception
            r5 = r8
            goto L_0x0376
        L_0x032c:
            r0 = move-exception
            r29 = r7
            r33 = r8
            r26 = r12
            r12 = r13
            r13 = r17
            r17 = r4
            r4 = r0
            com.google.firebase.crashlytics.FirebaseCrashlytics r0 = com.google.firebase.crashlytics.FirebaseCrashlytics.d()     // Catch:{ Exception -> 0x036f }
            r0.g(r4)     // Catch:{ Exception -> 0x036f }
            java.lang.String r0 = "Error"
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x036f }
            r7.<init>()     // Catch:{ Exception -> 0x036f }
            java.lang.String r8 = "Error in getNextEntry : "
            r7.append(r8)     // Catch:{ Exception -> 0x036f }
            java.lang.String r8 = r4.getLocalizedMessage()     // Catch:{ Exception -> 0x036f }
            r7.append(r8)     // Catch:{ Exception -> 0x036f }
            java.lang.String r7 = r7.toString()     // Catch:{ Exception -> 0x036f }
            net.imedicaldoctor.imd.iMDLogger.f(r0, r7)     // Catch:{ Exception -> 0x036f }
            r4.printStackTrace()     // Catch:{ Exception -> 0x036f }
            java.lang.String r0 = r4.getLocalizedMessage()     // Catch:{ Exception -> 0x036f }
            java.lang.String r7 = "CRC mismatch"
            boolean r0 = r0.equals(r7)     // Catch:{ Exception -> 0x036f }
            if (r0 == 0) goto L_0x036a
            goto L_0x031c
        L_0x036a:
            java.lang.String r0 = r4.getLocalizedMessage()     // Catch:{ Exception -> 0x036f }
            return r0
        L_0x036f:
            r0 = move-exception
            r5 = r20
            goto L_0x0376
        L_0x0373:
            r0 = move-exception
            r20 = r5
        L_0x0376:
            com.google.firebase.crashlytics.FirebaseCrashlytics r2 = com.google.firebase.crashlytics.FirebaseCrashlytics.d()
            r2.g(r0)
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = r0.getLocalizedMessage()
            r2.append(r3)
            java.lang.String r3 = " in "
            r2.append(r3)
            r2.append(r5)
            java.lang.String r2 = r2.toString()
            java.lang.String r4 = "Error in unzip"
            net.imedicaldoctor.imd.iMDLogger.f(r4, r2)
            r0.printStackTrace()
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r0 = r0.getLocalizedMessage()
            r2.append(r0)
            r2.append(r3)
            r2.append(r5)
            java.lang.String r0 = r2.toString()
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: net.imedicaldoctor.imd.Decompress.m(io.reactivex.rxjava3.core.ObservableEmitter, java.lang.String, android.app.Activity):java.lang.String");
    }
}
