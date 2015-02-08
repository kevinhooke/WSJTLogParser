package kh.radio.spotcollector.client;

import static org.junit.Assert.fail;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.WebServiceException;

import kh.radio.spotcollector.client.generated.Spot;
import kh.radio.spotcollector.client.generated.SpotCollectorEndpoint;
import kh.radio.spotcollector.client.generated.SpotCollectorEndpointService;

import org.junit.Test;

public class SpotCollectorWSClientTest {

	@Test
	public void testCallSpotStoreEndpoint() {
		SpotCollectorEndpointService service = new SpotCollectorEndpointService();
		SpotCollectorEndpoint endpoint = service.getSpotCollectorEndpointPort();

		List<Spot> spots = new ArrayList<>();
		spots.add(new Spot());
		endpoint.storeSpots(spots);
	}

	@Test
	public void testCallSpotStoreEndpointOpenShift_noWsdlUrl()
			throws MalformedURLException {

		try {
			SpotCollectorEndpointService service = new SpotCollectorEndpointService();

			SpotCollectorEndpoint endpoint = service
					.getSpotCollectorEndpointPort();

			BindingProvider bindingProvider = (BindingProvider) endpoint;
			bindingProvider
					.getRequestContext()
					.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
							"http://callsignviz2-kjh.rhcloud.com/SpotCollectorEndpoint");

			List<Spot> spots = new ArrayList<>();
			Spot spot = this.createTestSpot();
			spots.add(spot);

			endpoint.storeSpots(spots);
			
			fail("Expected WebServiceException because wsdl url was not passed");
			
			
		} catch (WebServiceException wse) {
			// expected exception as wsdl url was not passed
		}
	}

	@Test
	public void testCallSpotStoreEndpointOpenShift_withWsdlUrl()
			throws MalformedURLException {
		SpotCollectorEndpointService service = new SpotCollectorEndpointService(
				new URL(
						"http://callsignviz2-kjh.rhcloud.com/SpotCollectorEndpoint?wsdl"),
				new QName("http://endpoint.spotcollector.callsign.kh/",
						"SpotCollectorEndpointService"));

		SpotCollectorEndpoint endpoint = service.getSpotCollectorEndpointPort();

		BindingProvider bindingProvider = (BindingProvider) endpoint;
		bindingProvider.getRequestContext().put(
				BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
				"http://callsignviz2-kjh.rhcloud.com/SpotCollectorEndpoint");

		List<Spot> spots = new ArrayList<>();
		Spot spot = this.createTestSpot();
		spots.add(spot);
		endpoint.storeSpots(spots);
	}

	private Spot createTestSpot() {
		Spot spot = new Spot();
		spot.setTime((new Date()).toString());
		spot.setTime("1200");
		spot.setSignalreport("-10");
		spot.setFrequencyOffset("00");
		spot.setWord1("A1TEST");
		return spot;
	}

}
