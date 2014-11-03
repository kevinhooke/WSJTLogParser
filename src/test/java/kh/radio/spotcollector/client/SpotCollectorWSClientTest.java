package kh.radio.spotcollector.client;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import kh.radio.spotcollector.client.generated.Spot;
import kh.radio.spotcollector.client.generated.SpotCollectorEndpoint;
import kh.radio.spotcollector.client.generated.SpotCollectorEndpointService;

import org.junit.Test;

public class SpotCollectorWSClientTest {

	@Test
	public void testCallSpotStoreEndpoint(){
		SpotCollectorEndpointService service = new SpotCollectorEndpointService();
		SpotCollectorEndpoint endpoint = service.getSpotCollectorEndpointPort();
		
		List<Spot> spots = new ArrayList<>();
		spots.add(new Spot());
		endpoint.storeSpots(spots);
	}

	@Test
	public void testCallSpotStoreEndpointOpemShift() throws MalformedURLException{
		URL openShiftURL = new URL("http://callsignviz-kjh.rhcloud.com/SpotCollectorEndpoint");
		
		//TODO: need to package and install to mvn common jar
		
		SpotCollectorEndpointService service = new SpotCollectorEndpointService(openShiftURL);
		SpotCollectorEndpoint endpoint = service.getSpotCollectorEndpointPort();
		
		List<Spot> spots = new ArrayList<>();
		spots.add(new Spot());
		endpoint.storeSpots(spots);
	}

	
	
}
