package com.yongtrim.lib.model.postcode;

import android.os.AsyncTask;

import com.yongtrim.lib.model.list.Page;
import com.yongtrim.lib.util.MiscUtil;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.StringWriter;
import java.net.URL;

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
 * nuums / com.yongtrim.lib.model.postcode
 * <p/>
 * http://cafe.naver.com/citrineframework/1476
 * Created by Uihyun on 15. 12. 20..
 */
public class PostCodeTask extends AsyncTask<Void, Void, Void> {
    public final static String serviceKey = "b7P1WpFMuYvH9tmoSj6qL%2B7h6h7jXfYVNT248%2BlutNnYy5hvfJjeR%2Bru5bhkq26RWhAFC9%2FrsjM7xxTcMx5BPg%3D%3D";
    AServiceCallback callback;
    PostCodeList postCodeList;
    String keyword;
    int currentPage = 1;

    public PostCodeTask(String keyword, int page, AServiceCallback callback) {
        this.callback = callback;
        this.keyword = keyword;
        this.currentPage = page;
        postCodeList = new PostCodeList();
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
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... arg0) {
        StringBuffer urlString = new StringBuffer();

        urlString.append("http://openapi.epost.go.kr/postal/retrieveNewAdressAreaCdSearchAllService/retrieveNewAdressAreaCdSearchAllService/getNewAddressListAreaCdSearchAll");
        urlString.append("?ServiceKey=").append(serviceKey);


        urlString.append("&srchwrd=").append(MiscUtil.encode(keyword));

        urlString.append("&countPerPage=").append("50");
        urlString.append("&currentPage=").append("" + currentPage);


        try {

            URL url = new URL(urlString.toString());
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new InputSource(url.openStream()));
            doc.getDocumentElement().normalize();


            Element cmmMsgHeader = (Element) doc.getElementsByTagName("cmmMsgHeader").item(0);

            int totalPage = Integer.parseInt(cmmMsgHeader.getElementsByTagName("totalPage").item(0).getTextContent());
            int currentPage = Integer.parseInt(cmmMsgHeader.getElementsByTagName("currentPage").item(0).getTextContent());

            Page page = new Page();

            if (totalPage > currentPage) {
                page.setIsHasNext(true);
                page.setNext(currentPage + 1);
            } else {
                page.setIsHasNext(false);
                page.setNext(currentPage + 1);
            }

            postCodeList.setPages(page);

            NodeList nodeList = doc.getElementsByTagName("newAddressListAreaCdSearchAll");

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                Element fstElmnt = (Element) node;

                Node zipNo = fstElmnt.getElementsByTagName("zipNo").item(0);
                Node lnmAdres = fstElmnt.getElementsByTagName("lnmAdres").item(0);
                Node rnAdres = fstElmnt.getElementsByTagName("rnAdres").item(0);


                PostCode postCode = new PostCode();
                postCode.code = zipNo.getTextContent();
                postCode.oldAddress = rnAdres.getTextContent();
                postCode.newAddress = lnmAdres.getTextContent();

                postCodeList.getPostCodes().add(postCode);

                //Log.i("rr_", "Name = "+ ((Node) nameList.item(0)).getNodeValue());
            }


        } catch (Exception e) {
            System.out.println("XML Pasing Excpetion = " + e);
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        callback.success(postCodeList);
    }

    public interface AServiceCallback {
        void success(PostCodeList result);

        void failure(int errorCode, String message);
    }

}


