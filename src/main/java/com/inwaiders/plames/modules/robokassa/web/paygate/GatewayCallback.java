package com.inwaiders.plames.modules.robokassa.web.paygate;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.inwaiders.plames.modules.paygate.domain.billing.Bill;
import com.inwaiders.plames.modules.paygate.domain.billing.BillBase;
import com.inwaiders.plames.modules.robokassa.domain.RobokassaGateway;

@RestController
@RequestMapping("/api/robokassa/paygate/")
public class GatewayCallback {

	@PostMapping(value = "{id}/callback/result")
	public ResponseEntity<String> result(@PathVariable("id") long id, @RequestParam("OutSum") String sum, @RequestParam("InvId") long billId, @RequestParam("SignatureValue") String sigValue) throws NoSuchAlgorithmException {
		
		RobokassaGateway paygate = RobokassaGateway.getById(id);
	
		if(paygate == null) return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
		
		String mySigValue = new DigestUtils(MessageDigest.getInstance("SHA-256")).digestAsHex(sum+":"+billId+":"+paygate.getPass2());

		if(!mySigValue.toUpperCase().equals(sigValue.toUpperCase())) {
		
			return new ResponseEntity<String>("Bad sign", HttpStatus.BAD_REQUEST);
		}
		
		Bill bill = BillBase.getById(billId);
		
			if(!bill.isPaid()) {
		
				paygate.confirmPayment(bill);
			}
	
		return new ResponseEntity<String>("OK"+billId+"\n", HttpStatus.OK);
	}
}
