package com.example.hbctestapp;

import android.content.Intent;
import android.nfc.cardemulation.HostApduService;
import android.os.Bundle;
import android.os.Messenger;
import android.util.Log;

import java.util.Arrays;

public class CardService extends HostApduService {
    private static final String TAG = "CardService";
    private static final String SAMPLE_LOYALTY_CARD_AID = "F222222222";
    private static final String SELECT_APDU_HEADER = "00A40400";
    private static final byte[] SELECT_OK_SW = HexStringToByteArray("9000");
    private static final byte[] UNKNOWN_CMD_SW = HexStringToByteArray("0000");
    private static final byte[] SELECT_APDU = BuildSelectApdu(SAMPLE_LOYALTY_CARD_AID);

    private Messenger _handler;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // UI Thread 핸들 얻음
        Bundle extras = intent.getExtras();
        return START_STICKY;
    }

    // 이 메소드는 NFC 리더가 APDU(Application Protocol Data Unit)를 서비스로 전송할 때마다 호출 됨
    // APDU는 NFC 리더와 HCE 서비스 간 교환되는 App수준의 패킷임. 이 App수준 프로토콜은 반이중 방식
    // 즉, 리더는 APDU 명령을 전송하고 반응으로 응답 APDU를 수신할 때 까지 기다림
    @Override
    public byte[] processCommandApdu(byte[] commandApdu, Bundle extras) {
        if (Arrays.equals(SELECT_APDU, commandApdu)) {
            Log.i(TAG, "Application selected");
            Counter.AddOne();
            return SELECT_OK_SW;
        } else {
            return UNKNOWN_CMD_SW;
        }
    }
    /* 이 메소드는
    1. NFC 리더가 다른 'SELECT AID' APDU를 전송하고 OS가 이른 다른 서비스로 확인한다.
    2. NFC 리더와 기기 간의 NFC 링크가 끊어진다.
    두 가지 상황 모두에서 클래스의 onDeactivated() 구현이 두가지 중 어느 것이 발생했는지 나타내는 인수와
    함께 호출 됨
     */
    @Override
    public void onDeactivated(int reason) {
        Log.i(TAG, "Deactivated : " + reason);
    }
    public static byte[] BuildSelectApdu(String aid) {
        return HexStringToByteArray(SELECT_APDU_HEADER +
                String.format("%02X", aid.length() / 2) + aid);
    }

    public static byte[] HexStringToByteArray(String s) throws IllegalArgumentException {
        int len = s.length();
        if (len % 2 == 1) {
            throw new IllegalArgumentException("Hex string must have even number of characters");
        }
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit((s.charAt(i)), 16) << 4) +
                    Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }
}
