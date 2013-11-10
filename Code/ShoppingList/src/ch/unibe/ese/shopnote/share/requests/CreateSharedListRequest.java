package ch.unibe.ese.shopnote.share.requests;

public class CreateSharedListRequest extends Request {

	private static final long serialVersionUID = 7937903634262911605L;
	private int serverListId;
	private long localListId;
	private String listname;
	
	public CreateSharedListRequest(String phoneNumber, int serverListId, String listname) {
		super(phoneNumber);
		this.serverListId = serverListId;
		this.listname = listname;
	}

	@Override
	public int getType() {
		return Request.CREATE_SHARED_LIST_REQUEST;
	}

	public int getServerListid() {
		return this.serverListId;
	}
	
	public String getListName() {
		return this.listname;
	}
	
	public void setLocalListId(long id) {
		this.localListId = id;
	}
	
	public long getLocalListId() {
		return this.localListId;
	}
	
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "";
	}
	
}