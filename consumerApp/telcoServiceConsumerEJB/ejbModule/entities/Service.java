package entities;

import javax.persistence.*;

/**
 * Class for the FATHER entity of the Service Hierararchy. 
 * @author ubersandro
 *
 */
@Entity (name = "Service")
@Inheritance (strategy = InheritanceType.JOINED)
@DiscriminatorColumn (name = "serviceType", discriminatorType = DiscriminatorType.INTEGER)
@NamedQueries(
		{
			@NamedQuery (name = "Service.findAll", query = "SELECT s FROM Service s")
		})
public class Service {
	@Id @GeneratedValue (strategy = GenerationType.IDENTITY)
	 private int id; 
	 
//	 @OneToOne (mappedBy = "serviceID", fetch = FetchType.EAGER)
//	 private FixedInternetService fixedInternetService; 
//	 
//	 @OneToOne (mappedBy = "serviceID", fetch = FetchType.EAGER)
//	 private MobileInternetService mobileInternetService; 
//	 
//	 @OneToOne (mappedBy = "serviceID")
//	 private MobilePhoneService mobilePhoneService; 
//	 
//	 @OneToOne (mappedBy = "serviceID")
//	 private FixedPhoneService fixedPhoneService;

	 // THIS SIDE OF RELATIONSHIP W\ SERVICEPACKAGE IS NOT IMPLEMENTED 
	 
	public Service() {}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	} 
	
//	 public FixedInternetService getFixedInternetService() {
//		return fixedInternetService;
//	}
//
//	public void setFixedInternetService(FixedInternetService fixedInternetService) {
//		this.fixedInternetService = fixedInternetService;
//	}
//
//	public MobileInternetService getMobileInternetService() {
//		return mobileInternetService;
//	}
//
//	public void setMobileInternetService(MobileInternetService mobileInternetService) {
//		this.mobileInternetService = mobileInternetService;
//	}
//
//	public MobilePhoneService getMobilePhoneService() {
//		return mobilePhoneService;
//	}
//
//	public void setMobilePhoneService(MobilePhoneService mobilePhoneService) {
//		this.mobilePhoneService = mobilePhoneService;
//	}
//
//	public FixedPhoneService getFixedPhoneService() {
//		return fixedPhoneService;
//	}
//
//	public void setFixedPhoneService(FixedPhoneService fixedPhoneService) {
//		this.fixedPhoneService = fixedPhoneService;
//	}
//
//	
//	 
	 
}
