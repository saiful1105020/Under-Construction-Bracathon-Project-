<?php
class bracAdminModel extends CI_Model 
{
	
    public function __construct()	
	{
        $this->load->database();
	}
	
	
	public function get_loginInfo($data)	
	{
		$sql='SELECT * FROM bracadmin where `name` = ? and `password` = ?';
		$query = $this->db->query($sql,array($data['admin_name'],$data['password']));
		return $query;
	}
	
	public function get_all_locations()
	{
		$sql='SELECT DISTINCT neighbourhood FROM `location`';
		$query = $this->db->query($sql)->result_array();
		return $query;
	}
	
	/**
		Return 1 if password matched
		else return 0
	*/
	public function checkPassword($admin_name,$password)
	{
		$sql = 'SELECT password FROM bracadmin WHERE `name` = ? ';
		$query = $this->db->query($sql,array($admin_name))->row_array();
		$pass = $query['password'];
		
		if($pass === $password) return 1;
		else return 0;
	}
	
	public function changePassword($admin_name,$password)
	{
		$sql = 'UPDATE bracadmin SET password = ? WHERE `name` = ?';
		$query = $this->db->query($sql,array($password,$admin_name));
	}
	
	public function getAdminList()
	{
		$sql = "SELECT `admin_id`, `admin_name` FROM `admin`";
		$query = $this->db->query($sql)->result_array();
		return $query;
	}
	
	public function deleteAdmin($id)
	{
		$sql = "DELETE FROM `admin` WHERE `admin_id` = ?";
		$query = $this->db->query($sql,array($id));
	}
	
	public function addAdmin($name,$password)
	{
		$sql = "INSERT INTO `admin`(`admin_name`, `password`) VALUES (?,?)";
		$query = $this->db->query($sql,array($name,$password));
	}
	
}

?>