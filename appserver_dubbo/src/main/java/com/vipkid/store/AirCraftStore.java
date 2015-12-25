	
	package com.vipkid.store;

	import java.util.LinkedList;
	import java.util.List;

	import com.vipkid.model.AirCraft;
	import com.vipkid.model.AirCraftType;
	import com.vipkid.model.Student;

	public class AirCraftStore {
		private static AirCraftStore instance;
		private List<AirCraftType> aircraftTypes = new LinkedList<AirCraftType>(); 
		
		
		private AirCraftStore() {
			AirCraftType aircraftType = new AirCraftType();

			aircraftType.setSequence(1);
			aircraftTypes.add(aircraftType);

			
			//TODO 从文件读取petType列表
//			petTypes = GsonManager.getInstance().getGson().fromJson(json, PetType.class);
		}
		
		public synchronized static AirCraftStore getInstance() {
			if(instance == null) {
				instance = new AirCraftStore();
			}
			
			return instance;
		}
		
		public static AirCraft create(Student student, AirCraftStore aircraftType) {
			AirCraft aircraft = new AirCraft();
			aircraft.setSequence(aircraft.getSequence());
			aircraft.setStudent(student);
			
			return aircraft;
		}
		
		public List<AirCraftType> getAirCraftTypes() {
			return aircraftTypes;
		}
		
		
	}


