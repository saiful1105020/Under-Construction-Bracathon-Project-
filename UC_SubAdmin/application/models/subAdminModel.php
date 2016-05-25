<?php
class subAdminModel extends CI_Model 
{
	
    public function __construct()	
	{
        $this->load->database();
	}
	
	
	public function get_loginInfo($data)	
	{
		$sql='SELECT * FROM subadmin where `name` = ? and `password` = ?';
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
		/**
			All posts --- tested
		*/
		if($search_key['location']==="ANY" && $search_key['category']==="ANY" && $search_key['duration']==="ANY")
		{
			$sql='SELECT * FROM post WHERE status = 0 ORDER BY datediff(CURRENT_TIMESTAMP , time) ASC , COUNT_VOTES(post_id) DESC';
			$query = $this->db->query($sql)->result_array();
			return $query;
		}
		/**
			Search by location (Neighbourhood) -- tested
		*/
		else if($search_key['category']==="ANY" && $search_key['duration']==="ANY")
		{
			$sql='SELECT * FROM post where actual_location_id in (SELECT location_id FROM location WHERE neighbourhood = ?) and status = 0 
				ORDER BY datediff(CURRENT_TIMESTAMP , time) ASC , COUNT_VOTES(post_id) DESC';
			$query = $this->db->query($sql,array($search_key['location']))->result_array();
			return $query;
		}
		/**
			Search by category -- tested
		*/
		else if($search_key['location']==="ANY" && $search_key['duration']==="ANY")
		{
			$sql='SELECT * FROM post where category = ? and status = 0 ORDER BY datediff(CURRENT_TIMESTAMP , time) ASC , COUNT_VOTES(post_id) DESC';
			$query = $this->db->query($sql,array($search_key['category']))->result_array();
			return $query;
		}
		/**
			Search by duration -- tested
		*/
		else if($search_key['location']==="ANY" && $search_key['category']==="ANY")
		{
			$sql='SELECT * FROM post where CURRENT_TIMESTAMP < time + INTERVAL ? DAY  and status = 0 
				ORDER BY datediff(CURRENT_TIMESTAMP , time) ASC , COUNT_VOTES(post_id) DESC';
			$query = $this->db->query($sql,array($search_key['duration']))->result_array();
			return $query;
		}
		/**
			Search by location & category -- tested
		*/
		else if($search_key['duration']==="ANY")
		{
			$sql='SELECT * FROM post where actual_location_id in (SELECT location_id FROM location WHERE neighbourhood = ?) and category = ? 
				and status = 0 
				ORDER BY datediff(CURRENT_TIMESTAMP , time) ASC , COUNT_VOTES(post_id) DESC';
			$query = $this->db->query($sql,array($search_key['location'],$search_key['category']))->result_array();
			return $query;
		}
		/**
			Search by location & duration  --- tested
		*/
		else if($search_key['category']==="ANY")
		{
			$sql='SELECT * FROM post where actual_location_id in 
				(SELECT location_id FROM location WHERE neighbourhood = ?)
				and CURRENT_TIMESTAMP < time + INTERVAL ? DAY and status = 0 
				ORDER BY datediff(CURRENT_TIMESTAMP , time) ASC , COUNT_VOTES(post_id) DESC';

			$query = $this->db->query($sql,array($search_key['location'],$search_key['duration']))->result_array();
			return $query;
		}
		/**
			search by category & duration -- tested
		*/
		else if($search_key['location']==="ANY")
		{
			$sql='SELECT * FROM post where category = ? and CURRENT_TIMESTAMP < time + INTERVAL ? DAY 
				and status = 0 
				ORDER BY datediff(CURRENT_TIMESTAMP , time) ASC , COUNT_VOTES(post_id) DESC';
			
			$query = $this->db->query($sql,array($search_key['category'],$search_key['duration']))->result_array();
			return $query;
		}
		/**
			Search by duration , category & location
		*/
		else
		{
			$sql='SELECT * FROM post where CURRENT_TIMESTAMP < time + INTERVAL ? DAY
				and category = ? and actual_location_id in (SELECT location_id FROM location WHERE neighbourhood = ?) 
				and status = 0 
				ORDER BY datediff(CURRENT_TIMESTAMP , time) ASC , COUNT_VOTES(post_id) DESC';
			
			$query = $this->db->query($sql,array($search_key['duration'],$search_key['category'],$search_key['location']))->result_array();
			return $query;
		}
		
		/**
		SEARCH CONDITIONS:
		
			LOCATION : actual_location_id in (SELECT location_id FROM location WHERE neighbourhood = ?)

			CATEGORY : category = ?

			DURATION : CURRENT_TIMESTAMP < time + INTERVAL ? DAY

			
		*/
	}

	public function getAdminId($name)
	{
		$sql = "SELECT `id` FROM `subadmin` WHERE `name`=?";
		$query = $this->db->query($sql,array($name))->row_array();
		return $query['id'];
	}
}

?>