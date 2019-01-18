package com.lixinxin.nfcdemo;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.Ndef;
import android.os.Parcelable;
import android.provider.Settings;
import android.util.Log;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Locale;

public class NfcDao {

    private static final String LOG_TAG = "NfcDao";

    //nfc
    public static NfcAdapter mNfcAdapter;
    public static IntentFilter[] mIntentFilter = null;
    public static PendingIntent mPendingIntent = null;
    public static String[][] mTechList = null;

    /**
     * 构造函数
     */
    public NfcDao(Activity activity) {
        mNfcAdapter = NfcCheck(activity);
        NfcInit(activity);
    }

    /**
     * 检查NFC是否打开
     */
    public static NfcAdapter NfcCheck(Activity activity) {
        NfcAdapter mNfcAdapter = NfcAdapter.getDefaultAdapter(activity);
        if (mNfcAdapter == null) {
            return null;
        } else {
            if (!mNfcAdapter.isEnabled()) {
                Intent setNfc = new Intent(Settings.ACTION_NFC_SETTINGS);
                activity.startActivity(setNfc);
            }
        }
        return mNfcAdapter;
    }

    /**
     * 初始化nfc设置
     */
    public static void NfcInit(Activity activity) {
        mPendingIntent = PendingIntent.getActivity(activity, 0, new Intent(activity, activity.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter filter = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        IntentFilter filter2 = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        IntentFilter filter3 = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
        try {
            filter.addDataType("*/*");
        } catch (IntentFilter.MalformedMimeTypeException e) {
            e.printStackTrace();
        }
        mIntentFilter = new IntentFilter[]{filter, filter2, filter3};
        mTechList = null;
    }

    /**
     * 读nfc数据
     */
    public static String readFromTag(Intent intent) throws UnsupportedEncodingException {
        Parcelable[] rawArray = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        Parcelable[] rawArray1 = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_TAG);
        if (rawArray != null) {
            NdefMessage mNdefMsg = (NdefMessage) rawArray[0];
            NdefRecord mNdefRecord = mNdefMsg.getRecords()[0];
            if (mNdefRecord != null) {
                String readResult = new String(mNdefRecord.getPayload(), "UTF-8");
                return readResult;
            }
        }

        final Tag tag1 = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);


        //  Log.e("NfcDao", ByteArrayToHexString(tag1.getId()));

        if (rawArray1 != null) {

        }


        //nfc卡支持的格式
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        String[] temp = tag.getTechList();
        for (String s : temp) {
            // Log.e("----Dao", "resolveIntent tag: " + s);

            // m1卡 对应  android.nfc.tech.NfcA
        }


        return "没有数据";
    }


    /**
     * 写nfc数据
     */
    public static void writeToTag(String data, Intent intent) throws IOException, FormatException {
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        Ndef ndef = Ndef.get(tag);
        ndef.connect();
        NdefRecord ndefRecord = NdefRecord.createTextRecord(null, data);
        NdefRecord[] records = {ndefRecord};
        NdefMessage ndefMessage = new NdefMessage(records);
        ndef.writeNdefMessage(ndefMessage);
    }

    /**
     * 读nfcID
     */
    public static String readIdFromTag(Intent intent) throws UnsupportedEncodingException {
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        String id = ByteArrayToHexString(tag.getId());


        //----------------------------------------

        String data = "";

//        NfcA nfcA = NfcA.get(tag);
//
//        if (nfcA != null) {
//            Log.e(LOG_TAG, "支持nfcA");
//        } else {
//            Log.e(LOG_TAG, "不支持nfcA");
        //  }


//        NfcA nfca = NfcA.get(tag);
//        try {
//            nfca.connect();
//            if (nfca.isConnected()) {   //NTAG216的芯片
//                byte[] SELECT = {
//                        (byte) 0x30,
//                        (byte) 5 & 0x0ff,//0x05
//                };
//                byte[] response = nfca.transceive(SELECT);
//                nfca.close();
//                if (response != null) {
//                    data = ByteArrayToHexString(response);
//                    Log.e(LOG_TAG, data);
//                } else {
//                    Log.e(LOG_TAG, "没有数据");
//                }
//            } else {
//                Log.e(LOG_TAG, "不是NTAG216的芯片");
//            }
//
//
//        } catch (Exception e) {
//
//        } finally {
//            Log.e(LOG_TAG, "finally");
//        }


        Ndef ndef = Ndef.get(tag);
        if (ndef != null) {
            Log.e(LOG_TAG, "支持ndef");
        } else {
            Log.e(LOG_TAG, "不支持nef");
        }

        MifareClassic mifareClassic = MifareClassic.get(tag);

        byte[] datam = new byte[100];

        try {
            mifareClassic.connect();
            if (mifareClassic.isConnected()) {
                byte[] datamm = mifareClassic.transceive(datam);
                data = ByteArrayToHexString(datamm);
                Log.e(LOG_TAG, data);
                mifareClassic.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // NdefFormatable ndefFormatable = NdefFormatable.get(tag);

        return id;
    }


    public static String readTag(Intent intent) {
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        if (tag == null) {
            return "";
        }
        MifareClassic mfc = MifareClassic.get(tag);
        boolean auth = false;
        try {
            mfc.connect();
            String metaInfo = "";
            int type = mfc.getType();//获取TAG的类型  获得MifareClassic标签的具体类型

            int sectorCount = mfc.getSectorCount();//获取TAG中包含的扇区数
            String types = "";
            switch (type) {
                case MifareClassic.TYPE_CLASSIC:
                    types = "TYPE_CLASSIC";
                    break;
                case MifareClassic.TYPE_PLUS:
                    types = "TYPE_PLUS";
                    break;
                case MifareClassic.TYPE_PRO:
                    types = "TYPE_PRO";
                    break;
                case MifareClassic.TYPE_UNKNOWN:
                    types = "TYPE_UNKNOWN";
                    break;
            }
            //getBlockCount（）：获得标签总共有的的块数量；
            metaInfo += "卡片类型：" + types + "\n共" + sectorCount + "个扇区\n共"
                    + mfc.getBlockCount() + "个块\n存储空间: " + mfc.getSize()
                    + "B\n";
            for (int i = 0; i < sectorCount; i++) {
                //验证当前扇区的KeyA密码，返回值为ture或false。

                String key1 = "hdypdd";
                byte[] key11 = key1.getBytes();

                //auth = mfc.authenticateSectorWithKeyA(i, MifareClassic.KEY_DEFAULT);
                auth = mfc.authenticateSectorWithKeyA(i, key11);
                int bCount;
                int bIndex;
                if (auth) {
                    metaInfo += "Sector " + i + ":验证成功\n";
                    bCount = mfc.getBlockCountInSector(i);//获得当前扇区的所包含块的数量；
                    bIndex = mfc.sectorToBlock(i);//当前扇区的第1块的块号；
                    for (int j = 0; j < bCount; j++) {
                        //读取当前块的数据。
                        byte[] bytes = mfc.readBlock(bIndex);
                        //metaInfo += "Block " + bIndex + " : " + printHexBinary(bytes) + "\n";
                        // bIndex++;

                        //   Log.e("readTag", hexStringToStr(ByteArrayToHexString(bytes)));
                        // Log.e("readTag1", hexStringToStr(ByteArrayToHexString(bytes).replace("00", "")));


                        metaInfo += "Block " + bIndex + " : " + hexStringToStr(ByteArrayToHexString(bytes)) + "\n";
                        bIndex++;
                    }
                } else {
                    metaInfo += "Sector " + i + ":验证失败\n";
                }
            }
            return metaInfo;
        } catch (IOException e) {
            //Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } finally {
            if (mfc != null) {
                try {
                    mfc.close();
                } catch (IOException e) {
                    // Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }
        return null;
    }


    /**
     * 将字节数组转换为字符串
     */
    private static String ByteArrayToHexString(byte[] inarray) {
        int i, j, in;
        String[] hex = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F"};
        String out = "";

        for (j = 0; j < inarray.length; ++j) {
            in = (int) inarray[j] & 0xff;
            i = (in >> 4) & 0x0f;
            out += hex[i];
            i = in & 0x0f;
            out += hex[i];
        }
        return out.replace("00", "");
    }


    public static boolean readCard(Intent intent, int requesetCode) {
        final Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        // mTag = tag;
        if (tag != null) {
            //  ReaderManager.readCardExecute(requesetCode, tag, listener);
            return true;
        }
        return false;
    }


    public static String bytesToHexString(byte[] src, int size) {
        StringBuffer ret = new StringBuffer();
        if (src == null || size <= 0) {
            return null;
        }
        for (int i = 0; i < size; i++) {
            String hex = Integer.toHexString(src[i] & 0xFF);
            // String hex = String.format("%02x", src[i] & 0xFF);
            if (hex.length() < 2) {
                hex = "0" + hex;
            }
//            hex += "";
            ret.append(hex);
        }
        return ret.toString().toLowerCase(Locale.getDefault());
    }


    public static String toStringHex2(String s) {
        byte[] baKeyword = new byte[s.length() / 2];
        for (int i = 0; i < baKeyword.length; i++) {
            try {
                baKeyword[i] = (byte) (0xff & Integer.parseInt(s.substring(
                        i * 2, i * 2 + 2), 16));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            s = new String(baKeyword, "utf-8");// UTF-16le:Not
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return s;
    }

    public static String hexStringToStr(String s) {
        if (s == null || s.equals("")) {
            return null;
        }
        s = s.replace(" ", "");
        byte[] baKeyword = new byte[s.length() / 2];
        for (int i = 0; i < baKeyword.length; i++) {
            try {
                baKeyword[i] = (byte) (0xff & Integer.parseInt(s.substring(i * 2, i * 2 + 2), 16));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            s = new String(baKeyword, "gbk");
            new String();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return s;
    }

}
