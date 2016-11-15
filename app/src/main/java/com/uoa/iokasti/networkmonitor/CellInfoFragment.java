package com.uoa.iokasti.networkmonitor;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TextView;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class CellInfoFragment extends Fragment {

    private TableLayout cellInfoTableTableLayout;
    private TextView cellIdTextview;
    private TextView lacTextview;
    private TextView tacTextview;
    private TextView rssiTextview;
    private TextView rsrpTextview;
    private TextView rsrqTextview;
    private TextView rssnrTextview;
    private TextView cqiTextview;
    private TextView ratTextview;
    private TextView mccTextview;
    private TextView mncTextview;
    private TelephonyManager telephonyManager;

    private double latitude;
    private double longitude;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cell_info, container, false);
        findViews(view);

        TelephonyManager telephonyManager = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
        List<CellInfo> cellInfos = (List<CellInfo>) telephonyManager.getAllCellInfo();
        int cellId = Integer.MAX_VALUE;
        int lac = Integer.MAX_VALUE;
        int tac = Integer.MAX_VALUE;
        int rssi = Integer.MAX_VALUE;
        int rsrp = Integer.MAX_VALUE;
        int rsrq = Integer.MAX_VALUE;
        int rssnr = Integer.MAX_VALUE;
        int cqi = Integer.MAX_VALUE;
        String rat = "";
        int mcc = Integer.MAX_VALUE;
        int mnc = Integer.MAX_VALUE;
        for (CellInfo cellInfo : cellInfos) {
            if (cellInfo.isRegistered()) {
                if (cellInfo instanceof CellInfoGsm) {
                } else if (cellInfo instanceof CellInfoCdma) {
                } else if (cellInfo instanceof CellInfoWcdma) {
                } else if (cellInfo instanceof CellInfoLte) {
                    CellInfoLte cellInfoLte = (CellInfoLte) cellInfo;
                    cellId = cellInfoLte.getCellIdentity().getCi();
                    cellInfoTableTableLayout.removeView(view.findViewById(R.id.lac_row));
                    tac = cellInfoLte.getCellIdentity().getTac();
                    rssi = getRssi(cellInfoLte.getCellSignalStrength().toString());
                    rsrp = cellInfoLte.getCellSignalStrength().getDbm();
                    rsrq = getRsrq(cellInfoLte.getCellSignalStrength().toString());
                    rssnr = getRssnr(cellInfoLte.getCellSignalStrength().toString());
                    cqi = getCqi(cellInfoLte.getCellSignalStrength().toString());
                    rat = "LTE";
                    mcc = cellInfoLte.getCellIdentity().getMcc();
                    mnc = cellInfoLte.getCellIdentity().getMnc();
                    break;
                }
            }
        }

        cellIdTextview.setText(String.valueOf(cellId));
        tacTextview.setText(String.valueOf(tac));
        rssiTextview.setText(String.valueOf(rssi));
        rsrpTextview.setText(String.valueOf(rsrp));
        rsrqTextview.setText(String.valueOf(rsrq));
        rssnrTextview.setText(String.valueOf(rssnr));
        cqiTextview.setText(String.valueOf(cqi));
        ratTextview.setText(rat);
        mccTextview.setText(String.valueOf(mcc));
        mncTextview.setText(String.valueOf(mnc));
        return view;
    }

    private void findViews(View view) {
        cellInfoTableTableLayout = (TableLayout) view.findViewById(R.id.cell_info_table);
        cellIdTextview = (TextView) view.findViewById(R.id.cell_id_textview);
        lacTextview = (TextView) view.findViewById(R.id.lac_textview);
        tacTextview = (TextView) view.findViewById(R.id.tac_textview);
        rssiTextview = (TextView) view.findViewById(R.id.rssi_textview);
        rsrpTextview = (TextView) view.findViewById(R.id.rsrp_textview);
        rsrqTextview = (TextView) view.findViewById(R.id.rsrq_textview);
        rssnrTextview = (TextView) view.findViewById(R.id.rssnr_textview);
        cqiTextview = (TextView) view.findViewById(R.id.cqi_textview);
        ratTextview = (TextView) view.findViewById(R.id.rat_textview);
        mccTextview = (TextView) view.findViewById(R.id.mcc_textview);
        mncTextview = (TextView) view.findViewById(R.id.mnc_textview);
    }

    private int getRssi(String CellSignalStrengthLte) {
        return Integer.valueOf(CellSignalStrengthLte.substring(CellSignalStrengthLte.indexOf("ss=") + 3, CellSignalStrengthLte.indexOf("rsrp") - 1));
    }

    private int getRsrq(String CellSignalStrengthLte) {
        return Integer.valueOf(CellSignalStrengthLte.substring(CellSignalStrengthLte.indexOf("rsrq=") + 5, CellSignalStrengthLte.indexOf("rssnr") - 1));
    }

    private int getRssnr(String CellSignalStrengthLte) {
        return Integer.valueOf(CellSignalStrengthLte.substring(CellSignalStrengthLte.indexOf("rssnr=") + 6, CellSignalStrengthLte.indexOf("cqi") - 1));
    }

    private int getCqi(String CellSignalStrengthLte) {
        return Integer.valueOf(CellSignalStrengthLte.substring(CellSignalStrengthLte.indexOf("cqi=") + 4, CellSignalStrengthLte.indexOf("ta") - 1));
    }

    private void setLocation(int mcc, int mnc, int lac, int cellId, String radio) throws IOException {
        String openCellIdApiKey = "ef445193-fc82-482f-b199-9422b79a0e0a";
        String sUrl = "http://opencellid.org/cell/get?key=" + openCellIdApiKey + "&mcc=" +
                String.valueOf(mcc) + "&mnc=" + String.valueOf(mnc) + "&lac=" + String.valueOf(lac) +
                "&cellid=" + String.valueOf(cellId) + "&radio=" + radio + "&format=json";

        URL url = new URL(sUrl);
        HttpURLConnection request = (HttpURLConnection) url.openConnection();
        request.connect();


    }
}
