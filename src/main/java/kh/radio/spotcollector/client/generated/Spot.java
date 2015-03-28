package kh.radio.spotcollector.client.generated;

import java.time.LocalDateTime;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * <p>
 * Java class for spot complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name="spot">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="frequencyOffset" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="rxFrequency" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="signalreport" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="spotReceivedTimestamp" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="spotter" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="time" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="timeDeviation" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="word1" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="word2" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="word3" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "spot", propOrder = { "frequencyOffset", "id", "rxFrequency", "signalreport",
		"spotReceivedTimestamp", "spotter", "time", "timeDeviation", "word1", "word2", "word3" })
public class Spot {

	protected String frequencyOffset;
	protected String id;
	protected String rxFrequency;
	protected String signalreport;
	@XmlSchemaType(name = "dateTime")
	protected XMLGregorianCalendar spotReceivedTimestamp;
	protected String spotter;
	protected String time;
	protected String timeDeviation;
	protected String word1;
	protected String word2;
	protected String word3;

	/**
	 * KH added to generated source.
	 */
	public Spot() {

	}

	/**
	 * KH added to generated source.
	 */
	public Spot(XMLGregorianCalendar spotReceivedTimestamp, String rxFrequency, String time,
			String signalreport, String timeDeviation, String frequencyOffset, String word1,
			String word2, String word3) throws DatatypeConfigurationException {
		this.spotReceivedTimestamp = spotReceivedTimestamp;
		this.rxFrequency = rxFrequency;
		this.time = time;
		this.signalreport = signalreport;
		this.timeDeviation = timeDeviation;
		this.frequencyOffset = frequencyOffset;
		this.word1 = word1;
		this.word2 = word2;
		this.word3 = word3;
	}

	/**
	 * Gets the value of the frequencyOffset property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getFrequencyOffset() {
		return frequencyOffset;
	}

	/**
	 * Sets the value of the frequencyOffset property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setFrequencyOffset(String value) {
		this.frequencyOffset = value;
	}

	/**
	 * Gets the value of the id property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getId() {
		return id;
	}

	/**
	 * Sets the value of the id property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setId(String value) {
		this.id = value;
	}

	/**
	 * Gets the value of the rxFrequency property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getRxFrequency() {
		return rxFrequency;
	}

	/**
	 * Sets the value of the rxFrequency property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setRxFrequency(String value) {
		this.rxFrequency = value;
	}

	/**
	 * Gets the value of the signalreport property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getSignalreport() {
		return signalreport;
	}

	/**
	 * Sets the value of the signalreport property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setSignalreport(String value) {
		this.signalreport = value;
	}

	/**
	 * Gets the value of the spotReceivedTimestamp property.
	 * 
	 * @return possible object is {@link XMLGregorianCalendar }
	 * 
	 */
	public XMLGregorianCalendar getSpotReceivedTimestamp() {
		return spotReceivedTimestamp;
	}

	/**
	 * Sets the value of the spotReceivedTimestamp property.
	 * 
	 * @param value
	 *            allowed object is {@link XMLGregorianCalendar }
	 * 
	 */
	public void setSpotReceivedTimestamp(XMLGregorianCalendar value) {
		this.spotReceivedTimestamp = value;
	}

	/**
	 * Gets the value of the spotter property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getSpotter() {
		return spotter;
	}

	/**
	 * Sets the value of the spotter property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setSpotter(String value) {
		this.spotter = value;
	}

	/**
	 * Gets the value of the time property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getTime() {
		return time;
	}

	/**
	 * Sets the value of the time property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setTime(String value) {
		this.time = value;
	}

	/**
	 * Gets the value of the timeDeviation property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getTimeDeviation() {
		return timeDeviation;
	}

	/**
	 * Sets the value of the timeDeviation property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setTimeDeviation(String value) {
		this.timeDeviation = value;
	}

	/**
	 * Gets the value of the word1 property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getWord1() {
		return word1;
	}

	/**
	 * Sets the value of the word1 property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setWord1(String value) {
		this.word1 = value;
	}

	/**
	 * Gets the value of the word2 property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getWord2() {
		return word2;
	}

	/**
	 * Sets the value of the word2 property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setWord2(String value) {
		this.word2 = value;
	}

	/**
	 * Gets the value of the word3 property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getWord3() {
		return word3;
	}

	/**
	 * Sets the value of the word3 property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setWord3(String value) {
		this.word3 = value;
	}

}
