package com.inwaiders.plames.modules.robokassa.web.paygate;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller(value = "RobokassaGatewayListPage")
@RequestMapping("/robokassa/paygates")
public class GatewayListPage {

	@GetMapping("")
	public String mainPage(Model model) {

		return "robokassa_paygates";
	}
}