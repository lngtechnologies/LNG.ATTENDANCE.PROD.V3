package status;

public class Status {
	private Boolean error;
	private Integer code;
	private String message;
	
	public Status(Boolean flag, Integer code, String msg) {
		this.error = flag;
		this.code = code;
		this.message = msg;
	}
	
	public Boolean getError() {
		return error;
	}
	
	public void setError(Boolean error) {
		this.error = error;
	}
	
	public Integer getCode() {
		return code;
	}
	
	public void setCode(Integer code) {
		this.code = code;
	}
	
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	
}
