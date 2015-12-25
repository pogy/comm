package com.vipkid.ext.dby;


public class UpdateRoomTitleResult extends Result {
	private static final long serialVersionUID = 1L;
	
	private Room room;
	
	public Room getRoom() {
		return room;
	}
	public void setRoom(Room room) {
		this.room = room;
	}

    @Override
    public String toString() {
    	if (room != null){
    		return "UpdateRoomTitleResult{" +
                    "room=" + room.toString() +
                    '}';
    	}
    	return "UpdateRoomTitleResult failed";
        
    }
}
