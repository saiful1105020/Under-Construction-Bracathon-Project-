<?php
class Admin_model extends CI_Model 
{
	
    public function __construct()	//DONE
	{
        $this->load->database();
	}
		
	public function get_loginInfo($data)	//DONE
	{
		$sql='SELECT * FROM admin where `admin_name` = ? and `password` = ?';
		$query = $this->db->query($sql,array($data['admin_name'],$data['password']));
		return $query;
	}
}

?>