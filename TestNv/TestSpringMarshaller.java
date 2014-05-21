import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stream.StreamResult;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.castor.CastorMarshaller;

//import org.exolab.castor.xml.Marshaller;
import org.w3c.dom.Document;

public class TestSpringMarshaller {

    String filename = new String("testOutput.xml");
    CastorMarshaller marshaller;
    Object writeObject = null;

    public TestSpringMarshaller() {
    }

    public void setMarshaller(CastorMarshaller marshaller) {
        this.marshaller = marshaller;
    }

    public CastorMarshaller getMarshaller() {
        return marshaller;
    }

    public void setWriteObject(Object obj) {
        this.writeObject = obj;
    }
    
    public Object buildMockWriteObject() {
        FooMappings fm = new FooMappings();
        fm.setFooEnabled(true);
        return (Object)fm;
    }

    public Object buildDocWriteObject() {
        TestMappings tm = new TestMappings();
        tm.setIso("USD");
        tm.setName("US Dollar");
        tm.setSymbol("$");

        SubUnit su = new SubUnit();
        su.setName("Cents");
        tm.setSubUnit(su);

        Links links = new Links();
        ArrayList linkItems = new ArrayList();
        LinkItem link = new LinkItem();
        link.setHref("someurl.com/id/34939");
        linkItems.add(link);
        link.setHref("someurl.com/id/0839308");
        linkItems.add(link);
        link.setHref("someurl.com/id/3083");
        linkItems.add(link);
        link.setHref("someurl.com/id/309230");
        linkItems.add(link);
        links.setLinkItemList(linkItems);
        tm.setLinks(links);

        return (Object)tm;
    }

    public void writeObjToXML(Document doc) {
        DOMResult domResult = new DOMResult(doc);
        //marshaller.setResult(domResult);

        writeObjToXML();
    }

    public void writeObjToXML() {
        FileOutputStream fos = null;
        if (null == writeObject) {
            writeObject = buildDocWriteObject();
        }

        try {
            fos = new FileOutputStream(filename);
            marshaller.marshal(writeObject, new StreamResult(fos));
        } catch (IOException ie) {
            ie.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != fos) {
                    fos.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] argv) throws IOException {

        ApplicationContext testContext = new ClassPathXmlApplicationContext("testContext.xml");
        TestSpringMarshaller testMarshaller = (TestSpringMarshaller) testContext.getBean("castorMarshaller");
        testMarshaller.writeObjToXML();
    }

    
}







