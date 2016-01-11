package UserAccountWork;

public class UserAccount {
	
	
	public UserAccount(String _login, String _password,String _code, String _email,int _uniqid)
	{
		this._login = _login;
		this._password = _password;
		this._email = _email;
		this._uniqid = _uniqid;
		this._code=  _code;
	}
	
	
	private String _login;
	private String _password;
	private String _code;
	private String _email;
	private int _uniqid;
	
	
	public String getLogin() {
		return _login;
	}
	public void setLogin(String _login) {
		this._login = _login;
	}
	public String getPassword() {
		return _password;
	}
	public void setPassword(String _password) {
		this._password = _password;
	}
	public int getUniqid() {
		return _uniqid;
	}
	public void setUniqid(int _uniqid) {
		this._uniqid = _uniqid;
	}
	public String getCode() {
		return _code;
	}
	public void setCode(String _code) {
		this._code = _code;
	}
	public String getEmail() {
		return _email;
	}
	public void setEmail(String _email) {
		this._email = _email;
	}
	
	

}
