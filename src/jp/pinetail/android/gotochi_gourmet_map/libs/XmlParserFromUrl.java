package jp.pinetail.android.gotochi_gourmet_map.libs;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;


import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.util.Log;


public class XmlParserFromUrl {
    private static final String LOG_TAG = "XmlParserFromUrl";
    private XmlPullParser xpp;
    private XmlPullParserFactory factory;
    private int eventType;
    private String[] shop_codes = new String[200];
    private int iterator = 0;
    public int commentsCnt = 0;
    
    public XmlParserFromUrl() {
        try {
            factory = XmlPullParserFactory.newInstance();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
        factory.setNamespaceAware(true);
    }
    

    
    private String getText() throws XmlPullParserException, IOException {
        if (eventType != XmlPullParser.START_TAG) {
            eventType = xpp.next();
            return "UnKnown";
        }
        
        while (eventType != XmlPullParser.TEXT) {
            eventType = xpp.next();
        }
        
        return xpp.getText();
    }
    
    private void initXmlPullParser(String is) {
        try {
            
            xpp = factory.newPullParser();
//            xpp.setInput(is, "UTF-8");
            xpp.setInput(new StringReader(is));
                        
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }

        eventType = 0;
    }
    
    public ArrayList<ShopImages> getShopImages(String is) {
        ShopImages ret = new ShopImages();
        String tmpName = null;
        int flag = 0;
        ArrayList<ShopImages> list = new ArrayList<ShopImages>();
        String value = new String();

        if (is == null) {
            Log.d(LOG_TAG, "null!!");
            return null;
        }

        try {
            initXmlPullParser(is);
            while (eventType != XmlPullParser.END_DOCUMENT) {
                Log.i("hoge", String.valueOf(eventType));
                switch (eventType) {
                case XmlPullParser.START_DOCUMENT:
//                    alert += "Start document\n";
                    break;
                case XmlPullParser.END_DOCUMENT:
//                       alert += "End document\n";
                       break;
                case XmlPullParser.START_TAG:
                       if (xpp.getName().compareTo("Item") == 0) {
                           flag = 1;
                           ret = new ShopImages();
                       }
                    tmpName = xpp.getName();
//                       alert += "Start tag "+xpp.getName() + "\n";
                    break;
                case XmlPullParser.END_TAG:
//                       alert += "End tag "+xpp.getName() + "\n";
                       if (xpp.getName().compareTo("Item") == 0) {
                           flag = 0;
                           
                           list.add(ret);
                       } else if (flag == 1) {
                           ret.setData(tmpName, value);
                       }
                       break;
                case XmlPullParser.TEXT:
//                       alert += "Text "+xpp.getText() + "\n";
                       value = xpp.getText();
                       break;
                }
                
                eventType = xpp.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return list;
    }

    public ArrayList<Comments> getComments(String is) {
    	Comments ret = new Comments();
        String tmpName = null;
        int flag = 0;
        ArrayList<Comments> list = new ArrayList<Comments>();
        String value = new String();

        try {
            initXmlPullParser(is);
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                case XmlPullParser.START_DOCUMENT:
//                    alert += "Start document\n";
                    break;
                case XmlPullParser.END_DOCUMENT:
//                       alert += "End document\n";
                       break;
                case XmlPullParser.START_TAG:
                       if (xpp.getName().compareTo("Item") == 0) {
                           flag = 1;
                           ret = new Comments();
                       } else if (xpp.getName().compareTo("NumOfResult") == 0) {
                           flag = 2;
                       }
                    tmpName = xpp.getName();
//                       alert += "Start tag "+xpp.getName() + "\n";
                    break;
                case XmlPullParser.END_TAG:
//                       alert += "End tag "+xpp.getName() + "\n";
                       if (xpp.getName().compareTo("Item") == 0) {
                           flag = 0;
                           
                           list.add(ret);
                       } else if (flag == 1) {
                           ret.setData(tmpName, value);
                       } else if (xpp.getName().compareTo("NumOfResult") == 0) {
                           commentsCnt = Integer.parseInt(value);
                       }
                       break;
                case XmlPullParser.TEXT:
//                       alert += "Text "+xpp.getText() + "\n";
                       value = xpp.getText();
                       break;
                }
                
                eventType = xpp.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return list;
    }

    public HashMap<String,String> convertHashMap(String is) {
        
        String key = null;
        String value = null;
        HashMap<String,String> map = new HashMap<String,String>();

        try {
            initXmlPullParser(is);
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                case XmlPullParser.START_DOCUMENT:
                case XmlPullParser.END_DOCUMENT:
                    break;
                case XmlPullParser.END_TAG:
                    if (key != null && value != null) {
                        map.put(key, value);
                        key = null;
                        value = null;
                    }
                    break;
                case XmlPullParser.START_TAG:
                    key = xpp.getName();
                    break;
                case XmlPullParser.TEXT:
                    value = xpp.getText();
                    break;
                }
                
                eventType = xpp.next();
            }
            
        } catch (XmlPullParserException e) {
            // TODO 自動生成された catch ブロック
            e.printStackTrace();
        } catch (IOException e) {
            // TODO 自動生成された catch ブロック
            e.printStackTrace();
        }

        return map;
    }

    public HashMap<String,String> convertHashMapImage(String is) {
        
        String key = null;
        String value = null;
        String mode = null;
        HashMap<String,String> map = new HashMap<String,String>();

        try {    
            initXmlPullParser(is);
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                case XmlPullParser.START_DOCUMENT:
                case XmlPullParser.END_DOCUMENT:
                    break;
                case XmlPullParser.END_TAG:
                    if (key != null && key.equals("mode")) {
                        switch(Integer.parseInt(value)) {
                        case 0:
                            mode = "regular";
                            break;
                        case 1:
                            mode = "highoc";
                            break;
                        case 2:
                            mode = "diesel";
                            break;
                        case 3:
                            mode = "lamp";
                            break;
                        }
                        
                    } else if (key != null && (key.equals("min") || key.equals("max")) && value != null) {
                        
                        Util.logging("key:" + mode + "_" + key);
                        Util.logging("value:" + value);
                        map.put(mode + "_" + key, value);
                        key = null;
                        value = null;

                    }
                       break;
                case XmlPullParser.START_TAG:
                    key = xpp.getName();
                    break;
                case XmlPullParser.TEXT:
                    value = xpp.getText();
                       break;
                }
                
                eventType = xpp.next();
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }

        return map;
    }
}
