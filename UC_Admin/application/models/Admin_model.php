<?php
class Admin_model extends CI_Model 
{
	
    public function __construct()	//DONE
	{
        $this->load->database();
	}
		
	public function get_loginInfo($data)	//DONE
	{
		$query = $this->db->get_where('admin', array('admin_id' => $data['admin_id'],'password' => $data['password']));
		return $query;
	}
}

?>