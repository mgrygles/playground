import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.stream.StreamResult;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.castor.CastorMarshaller;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Entity;
import org.w3c.dom.Text;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * Test
 * 
 * 1) Read in an XML file (From command-line - default: ./test.xml) 
 * 2) Load into an org.w3c.dom.Document object
 * 3) Sort on the <link> element
 * 4) Write the transformed DOM document into a file or the console
 * 5) Unit test the code
 * 6) BONUS: write the transformed DOM document into HTML via servlet
 */
public class Test {

    String filename = new String("test.xml");
    File file = null;
    Document doc = null;

    public Test() {}

    public void setFilename(String f) {
        this.filename = f;
    }

    public String getFilename() {
        return this.filename;
    }        

    boolean parseFile() {
        boolean isValid = false;
        try {
            file = new File(getFilename());
            if (!file.exists() || !file.isFile()) {
                System.out.println("ERROR: Invalid input file. Please check that it exists and is a valid XML file.");
            } else {
                isValid = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return isValid;
    }


    public void loadXML() {
        try {
            if (null == doc) {
                if (!parseFile()) {
                    System.out.println("ERROR: Unable to parse XML File - " + getFilename());                    
                    throw new Exception("ERROR: Parse file");
                }
            }
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            doc = docBuilder.parse(file);
            doc.getDocumentElement().normalize();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void displayByTagName(String tagname) {

        try {

            NodeList nodeList = doc.getElementsByTagName(tagname);

            System.out.println("Root element :" + doc.getDocumentElement().getNodeName());

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                System.out.println("\nElement type: " + node.getNodeName());
            
                if (node.hasChildNodes()) {
                    displayChildNodes(node);
                }

                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element elmt = (Element) node;

                    if (node.hasAttributes()) {
                        System.out.println("element attribute: " + elmt.getAttribute("href"));
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void displayChildNodes(Node node) {
        NodeList c = node.getChildNodes();
        for (int i=0; i < c.getLength(); i++) {
            Node cnode = c.item(i);
            System.out.println("\nchild element type: " + cnode.getNodeName());
        }
    }


    public void sortLinks() {
        System.out.println("\nsort links");
    }

    public void writeXML() {
        ApplicationContext testContext = new ClassPathXmlApplicationContext("testContext.xml");
        TestSpringMarshaller marshaller = (TestSpringMarshaller) testContext.getBean("test");
        //testMarshaller.writeObjToXML(doc);
        marshaller.writeObjToXML();
    }



    public String getUsage() {
        StringBuilder sb = new StringBuilder();
        sb.append("***** Usage *****\n");
        sb.append(this.getClass().getName());
        sb.append(" <pathname of XML file> ");
        sb.append(" [optional: <name of tag> (default: link)]");
        return sb.toString();
    }


    public static void main(String[] argv) {

        Test theTest = new Test();
        String tag = "link";

        if (argv.length < 1) {
            System.out.println(theTest.getUsage());
            System.exit(-1);
        } else {
            if (argv.length == 1) {
                if ("-help".equals(argv[0])) {
                    System.out.println(theTest.getUsage());
                    System.exit(0);
                }
                System.out.println("tag name not provided, will use default (link)");
            } else {
                tag = argv[1];
            }
        }
        
        theTest.setFilename(argv[0]);
        if (theTest.parseFile()) {
            theTest.loadXML();
            theTest.displayByTagName(tag);
            theTest.sortLinks();
            theTest.writeXML();
        } else {
            System.out.println("ERROR: Failed processing. Sorry!");
        }

    }

}

