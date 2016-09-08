package com.yongtrim.lib.model.misc;

import android.os.AsyncTask;

import com.yongtrim.lib.model.user.LocationTask;
import com.yongtrim.lib.util.MiscUtil;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;


/**
 * hair / com.yongtrim.lib.model.misc
 * <p/>
 * Created by yongtrim.com on 15. 11. 29..
 */
public class Address extends AsyncTask<Void, Void, Void>
{
    public final static String TAG_NONE = " ";
    public final static String serviceKey = "b7P1WpFMuYvH9tmoSj6qL%2B7h6h7jXfYVNT248%2BlutNnYy5hvfJjeR%2Bru5bhkq26RWhAFC9%2FrsjM7xxTcMx5BPg%3D%3D";
    public final static String[] address1 = {"서울시", "경기도", "인천시", "부산시", "대구시", "대전시", "광주시", "울산시", "세종시", "경상남도", "경상북도", "전라남도", "전라북도", "충청남도", "충청북도", "강원도", "제주도"};
    public final static String[] address1_code = {"서울", "경기", "인천", "부산", "대구", "대전", "광주", "울산", "세종", "경남", "경북", "전남", "전북", "충남", "충북", "강원", "제주"};


    public interface AServiceCallback {
        void success(Object object);
        void failure(int errorCode, String message);
    }


    AServiceCallback callback;
    ArrayList<String> listResult;
    String address1Code;
    int step;

    public static String getAddress1Code(String string) {
        int index = MiscUtil.findIndex(address1, string);
        if(index >= 0)
            return address1_code[index];

        return "";
    }

    public Address(int step, String locationAddress1Code, AServiceCallback callback) {
        this.callback = callback;
        this.address1Code = locationAddress1Code;
        listResult = new ArrayList<>();
        this.step = step;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    public static String xmlToString(Node node) {
        try {
            Source source = new DOMSource(node);
            StringWriter stringWriter = new StringWriter();
            Result result = new StreamResult(stringWriter);
            TransformerFactory factory = TransformerFactory.newInstance();
            //factory.setAttribute("indent-number", "4");
            Transformer transformer = factory.newTransformer();
            transformer.transform(source, result);
            return stringWriter.getBuffer().toString();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected Void doInBackground(Void... arg0)
    {
        StringBuffer urlString = new StringBuffer();

        if(step == 1) {
            urlString.append("http://openapi.epost.go.kr/postal/retrieveLotNumberAdressService/retrieveLotNumberAdressService/getSiGunGuList");
            urlString.append("?ServiceKey=").append(serviceKey);

            String locationAddress1Code;
            //if(address1Code != null) {
                locationAddress1Code = address1Code;
            //}
//            else {
//                locationAddress1Code = shop.locationAddress1Code;
//            }

            urlString.append("&brtcCd=").append(MiscUtil.encode(locationAddress1Code));


        }
//        else if(step == 2) {
//            urlString.append("http://openapi.epost.go.kr/postal/retrieveLotNumberAdressService/retrieveLotNumberAdressService/getEupMyunDongList");
//            urlString.append("?ServiceKey=").append(serviceKey);
//
//            urlString.append("&brtcCd=").append(Utils.encode(shop.locationAddress1Code));
//            urlString.append("&signguCd=").append(Utils.encode(shop.locationAddress2));
//
//        }

        //Log.i("rr_", urlString.toString());

        try {

            URL url = new URL(urlString.toString());
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new InputSource(url.openStream()));
            doc.getDocumentElement().normalize();

            NodeList nodeList;

            if(step == 1) {
                nodeList = doc.getElementsByTagName("siGunGuList");
            } else {
                nodeList = doc.getElementsByTagName("eupMyunDongList");
            }

            for (int i = 0; i < nodeList.getLength(); i++) {

                Node node = nodeList.item(i);

                Element fstElmnt    = (Element)node;
                NodeList nameList;
                if(step == 1) {
                    nameList = fstElmnt.getElementsByTagName("signguCd");
                }
                else {
                    nameList = fstElmnt.getElementsByTagName("emdCd");
                }

                Element nameElement = (Element) nameList.item(0);
                nameList            = nameElement.getChildNodes();
                listResult.add(((Node) nameList.item(0)).getNodeValue());
                //Log.i("rr_", "Name = "+ ((Node) nameList.item(0)).getNodeValue());
            }

        } catch (Exception e) {
            System.out.println("XML Pasing Excpetion = " + e);
        }

        Collections.sort(listResult);

        if(listResult.size() == 0) {
            listResult.add(TAG_NONE);
        }

        if(listResult.size() == 1) {
            String string = listResult.get(0);

            if(string.length() == 0)
                listResult.set(0, TAG_NONE);
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);

        callback.success(listResult);
    }


    public static boolean hasAddressRealValue(String address) {
        return address.length() > 0 && !address.equals(TAG_NONE);
    }

    public static boolean hasAddressValue(String address) {
        return address.length() > 0;
    }
}
