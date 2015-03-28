
package kh.radio.spotcollector.client.generated;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the kh.radio.spotcollector.client.generated package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _JMSException_QNAME = new QName("http://endpoint.spotcollector.callsign.kh/", "JMSException");
    private final static QName _StoreSpots_QNAME = new QName("http://endpoint.spotcollector.callsign.kh/", "storeSpots");
    private final static QName _StoreSpotsResponse_QNAME = new QName("http://endpoint.spotcollector.callsign.kh/", "storeSpotsResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: kh.radio.spotcollector.client.generated
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link StoreSpots }
     * 
     */
    public StoreSpots createStoreSpots() {
        return new StoreSpots();
    }

    /**
     * Create an instance of {@link StoreSpotsResponse }
     * 
     */
    public StoreSpotsResponse createStoreSpotsResponse() {
        return new StoreSpotsResponse();
    }

    /**
     * Create an instance of {@link JMSException }
     * 
     */
    public JMSException createJMSException() {
        return new JMSException();
    }

    /**
     * Create an instance of {@link Spot }
     * 
     */
    public Spot createSpot() {
        return new Spot();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link JMSException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://endpoint.spotcollector.callsign.kh/", name = "JMSException")
    public JAXBElement<JMSException> createJMSException(JMSException value) {
        return new JAXBElement<JMSException>(_JMSException_QNAME, JMSException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link StoreSpots }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://endpoint.spotcollector.callsign.kh/", name = "storeSpots")
    public JAXBElement<StoreSpots> createStoreSpots(StoreSpots value) {
        return new JAXBElement<StoreSpots>(_StoreSpots_QNAME, StoreSpots.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link StoreSpotsResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://endpoint.spotcollector.callsign.kh/", name = "storeSpotsResponse")
    public JAXBElement<StoreSpotsResponse> createStoreSpotsResponse(StoreSpotsResponse value) {
        return new JAXBElement<StoreSpotsResponse>(_StoreSpotsResponse_QNAME, StoreSpotsResponse.class, null, value);
    }

}
