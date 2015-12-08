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
	
	public function get_all_locations()
	{
		$sql='SELECT DISTINCT neighbourhood FROM `location`';
		$query = $this->db->query($sql)->result_array();
		return $query;
	}
	
	public function search_post($search_key)
	{
		if($search_key['location']==="ANY" && $search_key['category']==="ANY" && $search_key['duration']==="ANY" && $search_key['status']==="ANY")
		{
			$sql='SELECT * FROM post';
			$query = $this->db->query($sql)->result_array();
			return $query;
		}
		else if($search_key['category']==="ANY" && $search_key['duration']==="ANY" && $search_key['status']==="ANY")
		{
			$sql='SELECT * FROM post where actual_location_id in (SELECT location_id FROM location WHERE neighbourhood = ?)';
			$query = $this->db->query($sql,array($search_key['location']))->result_array();
			return $query;
		}
		else if($search_key['location']==="ANY" && $search_key['duration']==="ANY" && $search_key['status']==="ANY")
		{
			$sql='SELECT * FROM post where category = ?';
			$query = $this->db->query($sql,array($search_key['category']))->result_array();
			return $query;
		}
		else if($search_key['location']==="ANY" && $search_key['category']==="ANY" && $search_key['status']==="ANY")
		{
			$sql='SELECT * FROM post where CURRENT_TIMESTAMP < time + INTERVAL ? DAY ORDER BY time DESC';
			$query = $this->db->query($sql,array($search_key['duration']))->result_array();
			return $query;
		}
		else if($search_key['location']==="ANY" && $search_key['duration']==="ANY" && $search_key['category']==="ANY")
		{
			$sql='SELECT * FROM post where status = ?';
			$query = $this->db->query($sql,array($search_key['status']))->result_array();
			return $query;
		}
	}
}

?>