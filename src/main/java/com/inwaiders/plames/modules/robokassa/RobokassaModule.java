package com.inwaiders.plames.modules.robokassa;

import com.inwaiders.plames.modules.paygate.domain.billing.gateway.PaymentGatewayHlRepository;
import com.inwaiders.plames.modules.robokassa.domain.RobokassaGateway;
import com.inwaiders.plames.modules.webcontroller.domain.module.WebDescribedModuleBase;
import com.inwaiders.plames.modules.webcontroller.domain.module.button.Button;

public class RobokassaModule extends WebDescribedModuleBase {

	private static RobokassaModule instance = new RobokassaModule();
	
	private RobokassaModule() {
		
		Button button = new Button();
			button.setName("Список шлюзов");
			button.setFontColor("#7892A3");
			button.setBackgroundColor("#BAE1FF");
			button.setBordersColor("#9EBFD8");
			button.setTargetPage("/robokassa/paygates");

		this.buttons.add(button);
	}
	
	@Override
	public void preInit() {
		
		PaymentGatewayHlRepository.addRepository(new RobokassaGateway.HighLevelRepository());
	}
	
	@Override
	public void init() {
		
		
	}

	@Override
	public String getName() {
		
		return "Robokassa Integration";
	}

	@Override
	public String getVersion() {
	
		return "1V";
	}

	@Override
	public String getDescription() {
		
		return "Модуль интеграции с платежным шлюзом \"Robokassa\".";
	}

	@Override
	public String getType() {
		
		return "integration";
	}

	@Override
	public String getLicenseKey() {
		
		return null;
	}

	@Override
	public long getSystemVersion() {
		
		return 0;
	}

	@Override
	public long getId() {
		
		return 8765236974L;
	}

	public static RobokassaModule getInstance() {
		
		return instance;
	}
}
