	
	package com.vipkid.store;

	import java.util.LinkedList;
	import java.util.List;

	import com.vipkid.model.AirCraftTheme;
	import com.vipkid.model.AirCraftThemeType;
	import com.vipkid.model.Student;

	public class AirCraftThemeStore {
		private static AirCraftThemeStore instance;
		private List<AirCraftThemeType> aircraftthemeTypes = new LinkedList<AirCraftThemeType>(); 
		
		
		private AirCraftThemeStore() {
			AirCraftThemeType aircraftthemeType = new AirCraftThemeType();
			
			aircraftthemeType.setIntroduction("The plane is the first plane.");
			aircraftthemeType.setLevel(1);
			aircraftthemeTypes.add(aircraftthemeType);

			
			//TODO 从文件读取petType列表
//			petTypes = GsonManager.getInstance().getGson().fromJson(json, PetType.class);
		}
		
		public synchronized static AirCraftThemeStore getInstance() {
			if(instance == null) {
				instance = new AirCraftThemeStore();
			}
			
			return instance;
		}
		
		public static AirCraftTheme create(Student student, AirCraftThemeStore aircraftType) {
			AirCraftTheme aircrafttheme = new AirCraftTheme();
			aircrafttheme.setCurrent(false);
			aircrafttheme.setLevel(aircrafttheme.getLevel());
			aircrafttheme.setIntroduction(aircrafttheme.getIntroduction());
			
			return aircrafttheme;
		}
		
		

		public List<AirCraftThemeType> getAirCraftTypes() {
			return aircraftthemeTypes;
		}
		
		
	}


