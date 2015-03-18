/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package radiostation.gui;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.persistence.EntityManager;
import javax.xml.transform.TransformerConfigurationException;
import radiostation.Playlist;
import radiostation.Song;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
/**
 *
 * @author user
 */
public class WriteXMLFile {
    
    private EntityManager em = null;
    private String filename="";
    public WriteXMLFile (EntityManager em) {
        this.em = em;
       
    }
       public EntityManager getEntityManager() {
        return em;
    }
    
    public static void WriteXMLFile(String fileName,Playlist playlist, List<Song>songs ){
    
        try {
 
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
 
		// top root element playlist
		Document doc = docBuilder.newDocument();
		Element rootElement = doc.createElement("playlist");
		doc.appendChild(rootElement);
                // playlist attributes
		
		Attr attrDescription = doc.createAttribute("Description");
                rootElement.setAttribute("Description",playlist.getName());
                                
                Attr attrDateCreated = doc.createAttribute("DateCreated");
                rootElement.setAttribute("DateCreated",new SimpleDateFormat ("dd/mm/yyyy").format(playlist.getCreationdate()));
                                             
                Element songNode=doc.getDocumentElement();
                
                //tree next node element song
                for (Song s: songs){//(int i=0;i<=songs.size();i++)
                    //create
                    Element song=doc.createElement("song");
                    rootElement.appendChild(song);
                    //set childElements
                    
                    Element songId = doc.createElement("id");
                    songId.appendChild(doc.createTextNode(s.getId().toString()));
                    song.appendChild(songId);
                    
                            
                    Element songTitle = doc.createElement("title");
                    songTitle.appendChild(doc.createTextNode(s.getTitle().toString()));
                    song.appendChild(songTitle);
                    
                    Element songDuration = doc.createElement("duration");
                    songDuration.appendChild(doc.createTextNode(Integer.toString(s.getDuration())));
                    song.appendChild(songDuration);
                    
                    Element songTrackNr = doc.createElement("trackNr");
                    songTrackNr.appendChild(doc.createTextNode(Integer.toString(s.getTracknr())));
                    song.appendChild(songTrackNr);
                        }
                //create xml 
                TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);    
                
                StreamResult result = new StreamResult(new File("D:\\"+fileName+".xml"));
 		transformer.transform(source, result);
                //message to user
		JOptionPane.showMessageDialog (null, "File saved in drive D!", "ΣΥΓΧΑΡΗΤΗΡΙΑ", JOptionPane.PLAIN_MESSAGE);
 
            }       catch (ParserConfigurationException ex) {
                        Logger.getLogger(WriteXMLFile.class.getName()).log(Level.SEVERE, null, ex);
            }       catch (TransformerConfigurationException ex) {
                        Logger.getLogger(WriteXMLFile.class.getName()).log(Level.SEVERE, null, ex);
            }       catch (TransformerException ex) {
                        Logger.getLogger(WriteXMLFile.class.getName()).log(Level.SEVERE, null, ex);
            }
    }
	
}
