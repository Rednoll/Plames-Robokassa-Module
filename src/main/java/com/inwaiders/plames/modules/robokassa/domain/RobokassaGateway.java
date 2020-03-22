package com.inwaiders.plames.modules.robokassa.domain;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.apache.commons.codec.digest.DigestUtils;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.inwaiders.plames.api.locale.PlamesLocale;
import com.inwaiders.plames.api.utils.DescribedFunctionResult;
import com.inwaiders.plames.api.utils.DescribedFunctionResult.Status;
import com.inwaiders.plames.modules.paygate.domain.billing.Bill;
import com.inwaiders.plames.modules.paygate.domain.billing.gateway.PaymentGateway;
import com.inwaiders.plames.modules.paygate.domain.billing.gateway.PaymentGatewayHlRepository;
import com.inwaiders.plames.modules.robokassa.dao.RobokassaGatewayRepository;

@Entity(name = "RobokassaGateway")
public class RobokassaGateway implements PaymentGateway {
	
	private static transient RobokassaGatewayRepository repository;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Long id;
	
	@Column(name = "name")
	private String name;
	
	@JsonAlias("merchant_login")
	@Column(name = "merchant_login")
	private String merchantLogin;
	
	@Column(name = "active")
	private boolean active;

	@Column(name = "pass_1")
	private String pass1 = null;
	
	@Column(name = "pass_2")
	private String pass2 = null;
	
	@JsonAlias("test_mode")
	@Column(name = "test_mode")
	private boolean testMode = false;
	
	@Column(name = "deleted")
	private volatile boolean deleted = false;
	
	public DescribedFunctionResult processBill(Bill bill) {

		String billUrl = null;
		
		try {
			
			billUrl = generateBillUrl(bill);
		}
		catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return new DescribedFunctionResult(Status.OK, "Отлично! Счет сформирован, вам осталось только его оплатить по ссылке: "+billUrl);
	}
	
	public void confirmPayment(Bill bill) {
		
		bill.setPaymentTime(System.currentTimeMillis());
		bill.onSuccess();
		bill.save();
	}

	private String generateBillUrl(Bill bill) throws UnsupportedEncodingException {
		
		String outSum = String.valueOf(((double) bill.getAmount()/100D));
		
		String signatureValue = getSignatureValue(bill.getId(), outSum);
		
		StringBuilder builder = new StringBuilder("https://auth.robokassa.ru/Merchant/Index.aspx");
			builder.append("?MerchantLogin="+merchantLogin);
			builder.append("&OutSum="+outSum);
			builder.append("&Description="+URLEncoder.encode(bill.getDescription(), "UTF-8"));
			builder.append("&InvId="+bill.getId());
			builder.append("&SignatureValue="+signatureValue);
			
			if(isTestMode()) {
				
				builder.append("&isTest=1");
			}
			
		return builder.toString();
	}
	
	public String getSignatureValue(long id, String outSum) {

		try {
			
			return new DigestUtils(MessageDigest.getInstance("SHA-256")).digestAsHex(merchantLogin+":"+outSum+":"+id+":"+pass1);
		}
		catch (NoSuchAlgorithmException e) {
			
			e.printStackTrace();
		}
		
		return null;
	}
	
	public void setTestMode(boolean i) {
		
		this.testMode = i;
	}
	
	public boolean isTestMode() {
		
		return this.testMode;
	}
	
	public void setMerchantLogin(String merchantLogin) {
		
		this.merchantLogin = merchantLogin;
	}
	
	public String getMerchantLogin() {
		
		return this.merchantLogin;
	}
	
	public void setPass2(String pass) {
		
		this.pass2 = pass;
	}
	
	public String getPass2() {
		
		return this.pass2;
	}
	
	public void setPass1(String pass) {
		
		this.pass1 = pass;
	}
	
	public String getPass1() {
		
		return this.pass1;
	}
	
	public void setName(String name) {
		
		this.name = name;
	}
	
	@Override
	public String getName() {
		
		return name;
	}
	
	public void setActive(boolean active) {
		
		this.active = active;
	}

	@Override
	public boolean isActive() {
		
		return this.active;
	}

	@Override
	public void save() {
		
		if(!deleted) {
			
			repository.save(this);
		}
	}

	@Override
	public void delete() {
		
		deleted = true;
		repository.save(this);
	}
	
	@Override
	public Long getId() {
		
		return this.id;
	}
	
	public String getDescription(PlamesLocale locale) {
	
		String result = "";
		
		if(isTestMode()) {
			
			result += "<span style=\"color: red;\">"+locale.getMessage("robokassa.paygate.test_active")+"</span>";
			result += "<br/>";
			result += "<span style=\"color: red;\">------------------------</span>";
			result += "<br/>";
		}
		
		result += locale.getMessage("name_word")+": "+getName();
		result += "<br/>";
		result += locale.getMessage("robokassa.paygate.merchant_login")+": "+getMerchantLogin();
		result += "<br/>";
		result += locale.getMessage("robokassa.paygate.pass1")+": "+getPass1();
		result += "<br/>";
		result += locale.getMessage("robokassa.paygate.pass2")+": "+getPass2();
		result += "<br/>";	
		
		return result;
	}
	
	public ObjectNode toJson(ObjectMapper mapper) {
		
		ObjectNode node = mapper.createObjectNode();
			node.put("name", getName());
			node.put("merchant_login", getMerchantLogin());
			node.put("pass1", getPass1());
			node.put("pass2", getPass2());
			node.put("test_mode", isTestMode());
			node.put("active", isActive());
			node.put("id", getId());
			
		return node;
	}
	
	public static List<RobokassaGateway> search(String name, Long id, String merchantLogin, Boolean testMode) {
		
		if(name != null) {
			
			name = name.toLowerCase();
		}
		
		if(merchantLogin != null) {
			
			merchantLogin = merchantLogin.toLowerCase();
		}
		
		String finalName = name;
		String finalMerchantLogin = merchantLogin;
		
		List<RobokassaGateway> result = getOrderedByName();
		
		result.removeIf((RobokassaGateway item)-> {
			
			if(finalName != null && !finalName.isEmpty()) {
				
				if(item.getName().toLowerCase().contains(finalName)) return false;
			}
			
			if(finalMerchantLogin != null && !finalMerchantLogin.isEmpty()) {
				
				if(item.getMerchantLogin().toLowerCase().contains(finalMerchantLogin)) return false;
			}
			
			if(testMode != null) {
				
				if(item.isTestMode() == testMode.booleanValue()) return false;
			}
			
			if(id != null) {
				
				if(item.getId().longValue() == id.longValue()) return false;
			
				if(String.valueOf(item.getId().longValue()).contains(String.valueOf(id.longValue()))) return false;
			}
			
			return true;
		});
		
		return result;
	}

	public static List<RobokassaGateway> getOrderedByName() {
		
		return repository.getOrderedByName();
	}
	
	public static RobokassaGateway create() {
		
		RobokassaGateway paygate = new RobokassaGateway();
		
			paygate = repository.save(paygate);
	
		return paygate;
	}
	
	public static RobokassaGateway getById(long id) {
		
		return repository.getOne(id);
	}
	
	public static List<RobokassaGateway> getAll() {
	
		return repository.findAll();
	}
	
	public static void setRepository(RobokassaGatewayRepository rep) {
		
		repository = rep;
	}
	
	public static class HighLevelRepository extends PaymentGatewayHlRepository {

		@Override
		public PaymentGateway create() {
			
			return RobokassaGateway.create();
		}

		@Override
		public PaymentGateway getById(long id) {
			
			return RobokassaGateway.getById(id);
		}

		@Override
		public List<PaymentGateway> getAll() {
			
			List<PaymentGateway> result = new ArrayList<>();
			
				result.addAll(RobokassaGateway.getAll());
			
			return result;
		}

		@Override
		public String getType() {
			
			return "robokassa";
		}
	}
}
