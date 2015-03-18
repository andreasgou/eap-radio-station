/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package radiostation.gui;
import java.io.File;
import org.w3c.dom.Document;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException; 
/**
 *
 * @author user
 */
/*public class ReadXMLFile () {
    
    public static void ReadXMLFile (){
    try { 
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse (new File("\D:\\file.xml\");

            
            doc.getDocumentElement ().normalize ();
            
            NodeList listOfSongs = doc.getElementsByTagName("song");
            
            for(int s=0; s<listOfSongs.getLength() ; s++){


                Node firstSongNode = listOfSongs.item(s);
                if(firstSongNode.getNodeType() == Node.ELEMENT_NODE){

                    Element firstSongElement = (Element)firstSongNode;

                    //-------
                    NodeList firstIdList = firstSongElement.getElementsByTagName("id");
                    Element firstIdElement = (Element)firstIdList.item(0);

                    NodeList textChildList  = firstSongElement.getChildNodes();
                    System.out.println("First Name : " + 
                           ((Node)textFNList.item(0)).getNodeValue().trim());

                    //-------
                    NodeList lastNameList = firstPersonElement.getElementsByTagName("last");
                    Element lastNameElement = (Element)lastNameList.item(0);

                    NodeList textLNList = lastNameElement.getChildNodes();
                    System.out.println("Last Name : " + 
                           ((Node)textLNList.item(0)).getNodeValue().trim());

                    //----
                    NodeList ageList = firstPersonElement.getElementsByTagName("age");
                    Element ageElement = (Element)ageList.item(0);

                    NodeList textAgeList = ageElement.getChildNodes();
                    System.out.println("Age : " + 
                           ((Node)textAgeList.item(0)).getNodeValue().trim());

                    //------


                }//end of if clause


            }//end of for loop with s var


        }catch (SAXParseException err) {
        System.out.println ("** Parsing error" + ", line " 
             + err.getLineNumber () + ", uri " + err.getSystemId ());
        System.out.println(" " + err.getMessage ());

        }catch (SAXException e) {
        Exception x = e.getException ();
        ((x == null) ? e : x).printStackTrace ();

        }catch (Throwable t) {
        t.printStackTrace ();
        }
     

    }


}*/
