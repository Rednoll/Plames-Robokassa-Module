package com.inwaiders.plames.modules.robokassa;

import com.inwaiders.plames.domain.module.impl.ModuleBase;
import com.inwaiders.plames.modules.paygate.domain.billing.gateway.PaymentGatewayHlRepository;
import com.inwaiders.plames.modules.robokassa.domain.RobokassaGateway;
import com.inwaiders.plames.modules.webcontroller.domain.module.BaseWebDescription;
import com.inwaiders.plames.modules.webcontroller.domain.module.WebDescribedModule;
import com.inwaiders.plames.modules.webcontroller.domain.module.WebDescription;
import com.inwaiders.plames.modules.webcontroller.domain.module.button.Button;

public class RobokassaModule extends ModuleBase implements WebDescribedModule {

	private static RobokassaModule instance = new RobokassaModule();
	
	private BaseWebDescription webDescription = new BaseWebDescription();
	
	private RobokassaModule() {
		
		Button button = new Button();
			button.setName("Список шлюзов");
			button.setFontColor("#7892A3");
			button.setBackgroundColor("#BAE1FF");
			button.setBordersColor("#9EBFD8");
			button.setTargetPage("/robokassa/paygates");

		webDescription.addButton(button);
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

	public WebDescription getWebDescription() {
		
		return this.webDescription;
	}
	
	public static RobokassaModule getInstance() {
		
		return instance;
	}
}
