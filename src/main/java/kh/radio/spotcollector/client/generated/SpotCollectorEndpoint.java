
package kh.radio.spotcollector.client.generated;

import java.util.List;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.9-b130926.1035
 * Generated source version: 2.2
 * 
 */
@WebService(name = "SpotCollectorEndpoint", targetNamespace = "http://endpoint.spotcollector.callsign.kh/")
@XmlSeeAlso({
    ObjectFactory.class
})
public interface SpotCollectorEndpoint {


    /**
     * 
     * @param arg0
     * @throws JMSException_Exception
     */
    @WebMethod
    @RequestWrapper(localName = "storeSpots", targetNamespace = "http://endpoint.spotcollector.callsign.kh/", className = "kh.radio.spotcollector.client.generated.StoreSpots")
    @ResponseWrapper(localName = "storeSpotsResponse", targetNamespace = "http://endpoint.spotcollector.callsign.kh/", className = "kh.radio.spotcollector.client.generated.StoreSpotsResponse")
    public void storeSpots(
        @WebParam(name = "arg0", targetNamespace = "")
        List<Spot> arg0)
        throws JMSException_Exception
    ;

}
