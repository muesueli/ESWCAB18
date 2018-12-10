package com.hu.eswcab18;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

class Global {

    private static int sprache = 0;
    private static MainActivity mainActivity;
    public static final Map<String, Float> exchangeRates = new HashMap<> ();
    private static org.w3c.dom.Document doc;
    public static String zeit;


    public Global(MainActivity mainActivity) {
        String spracheKurz = Locale.getDefault ().getLanguage ();
        if (spracheKurz.equals ("en")) {
            sprache = 1;
        }
        if (spracheKurz.equals ("de")) {
            sprache = 0;
        }

        Global.mainActivity = mainActivity;

    }

    public static String formatRate(float rate) {
        NumberFormat nf1 = NumberFormat.getNumberInstance (Locale.US);
        DecimalFormat df = (DecimalFormat) nf1;
        if (rate < 0.001) {
           return "<0.001";
        }
        if (rate < 1) {
            df.applyPattern ("###,###,###,##0.000");
        }
        if (rate >= 1 &&  rate < 10) {
            df.applyPattern ("###,###,###,##0.00");
        }
        if (rate >= 10 &&  rate < 1000) {
            df.applyPattern ("###,###,###,##0.0");
        }
        if (rate >= 1000) {
            df.applyPattern ("###,###,###,###");
        }
        String formatted = df.format (rate);
        return formatted;
    }

    public static Map<String, Float> maintainECB() {

        ConnectivityManager connectionManager = (ConnectivityManager) mainActivity.getSystemService (Context.CONNECTIVITY_SERVICE);
        try {
            if (connectionManager.getActiveNetworkInfo ().isConnected ()) {
                Log.e ("ConStatus", "Data Connection On");
            } else {
                Log.e ("ConStatus", "Data Connection off");
            }

            DownloadFileFromURL taskECB = new DownloadFileFromURL ();
            taskECB.execute ("https://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml").get ();
            // URL weiter unten gebraucht

            System.out.println ("ECB load vor Umrechnung " + Global.exchangeRates.toString ());
            float x1 = exchangeRates.get ("CHF"); // euro : chf
            float euroRate = x1;

            for (String symbol : exchangeRates.keySet ()) {
                float x2 = exchangeRates.get (symbol); // euro : foreign
                float x3 = x1 /x2 ;
                exchangeRates.put (symbol, x3);
            }
            exchangeRates.put ("EUR", euroRate);
            System.out.println ("ECB nach Umrechnunge " + Global.exchangeRates.toString ());

        } catch (Exception e) {
            System.out.println ("xDownload Error gettong local rates " + e);
        }
        return exchangeRates;
    }

/**
     * Scan through org.w3c.dom.Document document.
     */
    private static void visitDocument() {
        org.w3c.dom.Element element = doc.getDocumentElement ();
        if ((element != null) && element.getTagName ().equals ("gesmes:Envelope")) {
            visitElement_gesmes_Envelope (element);
        }
        if ((element != null) && element.getTagName ().equals ("gesmes:subject")) {
            visitElement_gesmes_subject (element);
        }
        if ((element != null) && element.getTagName ().equals ("gesmes:Sender")) {
            visitElement_gesmes_Sender (element);
        }
        if ((element != null) && element.getTagName ().equals ("gesmes:name")) {
            visitElement_gesmes_name (element);
        }
        if ((element != null) && element.getTagName ().equals ("Cube")) {
            visitElement_Cube (element);
        }
    }

    /**
     * Scan through org.w3c.dom.Element named gesmes:Envelope.
     */
    private static void visitElement_gesmes_Envelope(org.w3c.dom.Element element) {
        // <gesmes:Envelope>
        // element.getValue();
        // element.getValue();
        org.w3c.dom.NamedNodeMap attrs = element.getAttributes ();
        for (int i = 0; i < attrs.getLength (); i++) {
            org.w3c.dom.Attr attr = (org.w3c.dom.Attr) attrs.item (i);
            if (attr.getName ().equals ("xmlns:gesmes")) {
                // <gesmes:Envelope xmlns:gesmes="???">
                // attr.getValue();
            }
            if (attr.getName ().equals ("xmlns")) {
                // <gesmes:Envelope xmlns="???">
                // attr.getValue();
            }
        }
        org.w3c.dom.NodeList nodes = element.getChildNodes ();
        for (int i = 0; i < nodes.getLength (); i++) {
            org.w3c.dom.Node node = nodes.item (i);
            switch (node.getNodeType ()) {
                case org.w3c.dom.Node.CDATA_SECTION_NODE:
                    // ((org.w3c.dom.CDATASection)node).getData();
                    break;
                case org.w3c.dom.Node.ELEMENT_NODE:
                    org.w3c.dom.Element nodeElement = (org.w3c.dom.Element) node;
                    if (nodeElement.getTagName ().equals ("gesmes:subject")) {
                        visitElement_gesmes_subject (nodeElement);
                    }
                    if (nodeElement.getTagName ().equals ("gesmes:Sender")) {
                        visitElement_gesmes_Sender (nodeElement);
                    }
                    if (nodeElement.getTagName ().equals ("Cube")) {
                        visitElement_Cube (nodeElement);
                    }
                    break;
                case org.w3c.dom.Node.PROCESSING_INSTRUCTION_NODE:
                    // ((org.w3c.dom.ProcessingInstruction)node).getTarget();
                    // ((org.w3c.dom.ProcessingInstruction)node).getData();
                    break;
            }
        }
    }

    /**
     * Scan through org.w3c.dom.Element named gesmes:subject.
     */
    private static void visitElement_gesmes_subject(org.w3c.dom.Element element) {
        // <gesmes:subject>
        // element.getValue();
        // element.getValue();
        org.w3c.dom.NodeList nodes = element.getChildNodes ();
        for (int i = 0; i < nodes.getLength (); i++) {
            org.w3c.dom.Node node = nodes.item (i);
            switch (node.getNodeType ()) {
                case org.w3c.dom.Node.CDATA_SECTION_NODE:
                    // ((org.w3c.dom.CDATASection)node).getData();
                    break;
                case org.w3c.dom.Node.ELEMENT_NODE:
                    org.w3c.dom.Element nodeElement = (org.w3c.dom.Element) node;
                    break;
                case org.w3c.dom.Node.PROCESSING_INSTRUCTION_NODE:
                    // ((org.w3c.dom.ProcessingInstruction)node).getTarget();
                    // ((org.w3c.dom.ProcessingInstruction)node).getData();
                    break;
                case org.w3c.dom.Node.TEXT_NODE:
                    // ((org.w3c.dom.Text)node).getData();
                    break;
            }
        }
    }

    /**
     * Scan through org.w3c.dom.Element named gesmes:Sender.
     */
    private static void visitElement_gesmes_Sender(org.w3c.dom.Element element) {
        // <gesmes:Sender>
        // element.getValue();
        // element.getValue();
        org.w3c.dom.NodeList nodes = element.getChildNodes ();
        for (int i = 0; i < nodes.getLength (); i++) {
            org.w3c.dom.Node node = nodes.item (i);
            switch (node.getNodeType ()) {
                case org.w3c.dom.Node.CDATA_SECTION_NODE:
                    // ((org.w3c.dom.CDATASection)node).getData();
                    break;
                case org.w3c.dom.Node.ELEMENT_NODE:
                    org.w3c.dom.Element nodeElement = (org.w3c.dom.Element) node;
                    if (nodeElement.getTagName ().equals ("gesmes:name")) {
                        visitElement_gesmes_name (nodeElement);
                    }
                    break;
                case org.w3c.dom.Node.PROCESSING_INSTRUCTION_NODE:
                    // ((org.w3c.dom.ProcessingInstruction)node).getTarget();
                    // ((org.w3c.dom.ProcessingInstruction)node).getData();
                    break;
            }
        }
    }

    /**
     * Scan through org.w3c.dom.Element named gesmes:name.
     */
    private static void visitElement_gesmes_name(org.w3c.dom.Element element) {
        // <gesmes:name>
        // element.getValue();
        // element.getValue();
        org.w3c.dom.NodeList nodes = element.getChildNodes ();
        for (int i = 0; i < nodes.getLength (); i++) {
            org.w3c.dom.Node node = nodes.item (i);
            switch (node.getNodeType ()) {
                case org.w3c.dom.Node.CDATA_SECTION_NODE:
                    // ((org.w3c.dom.CDATASection)node).getData();
                    break;
                case org.w3c.dom.Node.ELEMENT_NODE:
                    org.w3c.dom.Element nodeElement = (org.w3c.dom.Element) node;
                    break;
                case org.w3c.dom.Node.PROCESSING_INSTRUCTION_NODE:
                    // ((org.w3c.dom.ProcessingInstruction)node).getTarget();
                    // ((org.w3c.dom.ProcessingInstruction)node).getData();
                    break;
                case org.w3c.dom.Node.TEXT_NODE:
                    // ((org.w3c.dom.Text)node).getData();
                    break;
            }
        }
    }

    /**
     * Scan through org.w3c.dom.Element named Cube.
     */
    private static void visitElement_Cube(org.w3c.dom.Element element) {
        // <Cube>
        // element.getValue();
        // element.getValue();
        org.w3c.dom.NamedNodeMap attrs = element.getAttributes ();
        for (int i = 0; i < attrs.getLength (); i++) {
            org.w3c.dom.Attr attr = (org.w3c.dom.Attr) attrs.item (i);
            if (attr.getName ().equals ("rate")) {
                // <Cube rate="???">
                // attr.getValue();
            }
            if (attr.getName ().equals ("currency")) {
                String currency = attr.getValue ();
                float rate = Float.parseFloat (element.getAttribute ("rate"));
                if (currency.equals ("JPY")) {
                    // rate = 4.0f;
                }
                if (currency.equals ("CHF")) {
                    // rate = 2.00f;
                }
                if (currency.equals ("USD")) {
                    // rate = 0.5f;
                }
                exchangeRates.put (currency, rate);
            }
            if (attr.getName().equals("time")) {
                zeit = attr.getValue();
            }
        }
        org.w3c.dom.NodeList nodes = element.getChildNodes ();
        for (int i = 0; i < nodes.getLength (); i++) {
            org.w3c.dom.Node node = nodes.item (i);
            switch (node.getNodeType ()) {
                case org.w3c.dom.Node.CDATA_SECTION_NODE:
                    // ((org.w3c.dom.CDATASection)node).getData();
                    break;
                case org.w3c.dom.Node.ELEMENT_NODE:
                    org.w3c.dom.Element nodeElement = (org.w3c.dom.Element) node;
                    if (nodeElement.getTagName ().equals ("Cube")) {
                        visitElement_Cube (nodeElement);
                    }
                    break;
                case org.w3c.dom.Node.PROCESSING_INSTRUCTION_NODE:
                    // ((org.w3c.dom.ProcessingInstruction)node).getTarget();
                    // ((org.w3c.dom.ProcessingInstruction)node).getData();
                    break;
            }
        }
    }

    private static final String ezbPath = "https://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml";

static class DownloadFileFromURL extends AsyncTask<String, String, String> {


//        private ProgressDialog pDialog;

    /**
     * Before starting background thread
     */
    @Override
    protected void onPreExecute() {
        super.onPreExecute ();
        System.out.println ("xDownload Starting");

//            pDialog = new ProgressDialog (SwissBillsActivity.this);
//            pDialog.setMessage ("Loading... Please wait...");
//            pDialog.setIndeterminate (false);
//            pDialog.setCancelable (false);
//            pDialog.show ();
    }

    /**
     * Downloading file in background thread
     */
    @Override
    protected String doInBackground(String... f_url) {
        int count;
        File out;

        try {
            System.out.println ("xDownloading");
            URL urlezb = new URL (ezbPath);
            URLConnection connection = urlezb.openConnection ();
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance ();
            DocumentBuilder builder = factory.newDocumentBuilder ();
            InputStream is = connection.getInputStream ();

//                final BufferedReader reader = new BufferedReader (
//                        new InputStreamReader (is));
//                String line = null;
//                while ((line = reader.readLine()) != null) {
//                    System.out.println(line);
//                }
//                reader.close();

            doc = builder.parse (is);

            visitDocument ();

        } catch (Exception e) {
            System.out.println ("xDownload Error: " + e.getMessage ());
        }

        return "Downloaded doInBackground";
    }

    /**
     * After completing background task
     **/
    @Override
    protected void onPostExecute(String file_url) {
        System.out.println ("xDownload downloaded");

//            pDialog.dismiss ();
//        Snackbar.make (view, "Here's a Snackbar", Snackbar.LENGTH_LONG)
//                .setAction ("Action", null).show ();

    }

}


    }
