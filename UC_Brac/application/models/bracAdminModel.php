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
	
}

?>