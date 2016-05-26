<?php
class Admin_model extends CI_Model 
{
	
    public function __construct()
	{
        $this->load->database();
	}
	
	/**
		Returns the list of distinct neighbourhood.
		If empty -> a null array is returned
	*/
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
		$sql = 'SELECT password FROM admin WHERE admin_name = ? ';
		$query = $this->db->query($sql,array($admin_name))->row_array();
		$pass = $query['password'];
		
		if($pass === $password) return 1;
		else return 0;
	}
	
	public function changePassword($admin_name,$password)
	{
		$sql = 'UPDATE admin SET password = ? WHERE admin_name = ?';
		$query = $this->db->query($sql,array($password,$admin_name));
	}
	
	/**
		Returns search result . don't show the posts filtered by sub-admin
	*/
	public function search_post($search_key)
	{
		/**
			Show all posts
		*/
		if($search_key['location']==="ANY" && $search_key['category']==="ANY" && $search_key['duration']==="ANY" && $search_key['status']==="ANY")
		{
			$sql='SELECT * FROM post where `flag` = 0 ORDER BY datediff(CURRENT_TIMESTAMP , time) ASC , COUNT_VOTES(post_id) DESC';
			$query = $this->db->query($sql)->result_array();
			return $query;
		}
		/**
			Search by location (Neighbourhood)
		*/
		else if($search_key['category']==="ANY" && $search_key['duration']==="ANY" && $search_key['status']==="ANY")
		{
			$sql='SELECT * FROM post where `flag` = 0 and actual_location_id in (SELECT location_id FROM location WHERE neighbourhood = ?)
				ORDER BY datediff(CURRENT_TIMESTAMP , time) ASC , COUNT_VOTES(post_id) DESC';
			$query = $this->db->query($sql,array($search_key['location']))->result_array();
			return $query;
		}
		/**
			Search by category
		*/
		else if($search_key['location']==="ANY" && $search_key['duration']==="ANY" && $search_key['status']==="ANY")
		{
			$sql='SELECT * FROM post where category = ? and `flag` = 0 ORDER BY datediff(CURRENT_TIMESTAMP , time) ASC , COUNT_VOTES(post_id) DESC';
			$query = $this->db->query($sql,array($search_key['category']))->result_array();
			return $query;
		}
		/**
			Search by duration
		*/
		else if($search_key['location']==="ANY" && $search_key['category']==="ANY" && $search_key['status']==="ANY")
		{
			$sql='SELECT * FROM post where `flag` = 0 and CURRENT_TIMESTAMP < time + INTERVAL ? DAY 
				ORDER BY datediff(CURRENT_TIMESTAMP , time) ASC , COUNT_VOTES(post_id) DESC';
			$query = $this->db->query($sql,array($search_key['duration']))->result_array();
			return $query;
		}
		/**
			Search by duration
		*/
		else if($search_key['location']==="ANY" && $search_key['duration']==="ANY" && $search_key['category']==="ANY")
		{
			$sql='SELECT * FROM post where status = ? and `flag` = 0 ORDER BY datediff(CURRENT_TIMESTAMP , time) ASC , COUNT_VOTES(post_id) DESC';
			$query = $this->db->query($sql,array($search_key['status']))->result_array();
			return $query;
		}
		/**
			Search by location & category
		*/
		else if($search_key['duration']==="ANY" && $search_key['status']==="ANY")
		{
			$sql='SELECT * FROM post where `flag` = 0 and actual_location_id in (SELECT location_id FROM location WHERE neighbourhood = ?) and category = ? 
				ORDER BY datediff(CURRENT_TIMESTAMP , time) ASC , COUNT_VOTES(post_id) DESC';
			$query = $this->db->query($sql,array($search_key['location'],$search_key['category']))->result_array();
			return $query;
		}
		/**
			Search by location & duration
		*/
		else if($search_key['category']==="ANY" && $search_key['status']==="ANY")
		{
			$sql='SELECT * FROM post where `flag` = 0 and actual_location_id in 
				(SELECT location_id FROM location WHERE neighbourhood = ?)
				and CURRENT_TIMESTAMP < time + INTERVAL ? DAY 
				ORDER BY datediff(CURRENT_TIMESTAMP , time) ASC , COUNT_VOTES(post_id) DESC';

			$query = $this->db->query($sql,array($search_key['location'],$search_key['duration']))->result_array();
			return $query;
		}
		/**
			Search by location & status
		*/
		else if($search_key['category']==="ANY" && $search_key['duration']==="ANY")
		{
			$sql='SELECT * FROM post where `flag` = 0 and status = ? 
				and actual_location_id in (SELECT location_id FROM location WHERE neighbourhood = ?)
				ORDER BY datediff(CURRENT_TIMESTAMP , time) ASC , COUNT_VOTES(post_id) DESC';
			$query = $this->db->query($sql,array($search_key['status'],$search_key['location']))->result_array();
			return $query;
		}
		/**
			search by category & duration
		*/
		else if($search_key['location']==="ANY" && $search_key['status']==="ANY")
		{
			$sql='SELECT * FROM post where `flag` = 0 and category = ? and CURRENT_TIMESTAMP < time + INTERVAL ? DAY 
				ORDER BY datediff(CURRENT_TIMESTAMP , time) ASC , COUNT_VOTES(post_id) DESC';
			
			$query = $this->db->query($sql,array($search_key['category'],$search_key['duration']))->result_array();
			return $query;
		}
		/**
			search by category & status
		*/
		else if($search_key['location']==="ANY" && $search_key['duration']==="ANY")
		{
			$sql='SELECT * FROM post where `flag` = 0 and category = ? and status = ? 
				ORDER BY datediff(CURRENT_TIMESTAMP , time) ASC , COUNT_VOTES(post_id) DESC';
			$query = $this->db->query($sql,array($search_key['category'],$search_key['status']))->result_array();
			return $query;
		}
		/**
			search by duration & status
		*/
		else if($search_key['location']==="ANY" && $search_key['category']==="ANY")
		{
			$sql='SELECT * FROM post where `flag` = 0 and CURRENT_TIMESTAMP < time + INTERVAL ? DAY and status = ? 
				ORDER BY datediff(CURRENT_TIMESTAMP , time) ASC , COUNT_VOTES(post_id) DESC';
			$query = $this->db->query($sql,array($search_key['duration'],$search_key['status']))->result_array();
			return $query;
		}
		/**
			Search by duration , category & location
		*/
		else if($search_key['status']==="ANY")
		{
			$sql='SELECT * FROM post where `flag` = 0 and CURRENT_TIMESTAMP < time + INTERVAL ? DAY
				and category = ? and actual_location_id in (SELECT location_id FROM location WHERE neighbourhood = ?) 
				ORDER BY datediff(CURRENT_TIMESTAMP , time) ASC , COUNT_VOTES(post_id) DESC';
			
			$query = $this->db->query($sql,array($search_key['duration'],$search_key['category'],$search_key['location']))->result_array();
			return $query;
		}
		/**
			Search by status , category & location
		*/
		else if($search_key['duration']==="ANY")
		{
			$sql='SELECT * FROM post where status = ?
				and category = ? and `flag` = 0 and actual_location_id in (SELECT location_id FROM location WHERE neighbourhood = ?) 
				ORDER BY datediff(CURRENT_TIMESTAMP , time) ASC , COUNT_VOTES(post_id) DESC';
			
			$query = $this->db->query($sql,array($search_key['status'],$search_key['category'],$search_key['location']))->result_array();
			return $query;
		}
		/**
			Search by duration , category & status
		*/
		else if($search_key['location']==="ANY")
		{
			$sql='SELECT * FROM post where `flag` = 0 and status = ?
				and category = ? and CURRENT_TIMESTAMP < time + INTERVAL ? DAY 
				ORDER BY datediff(CURRENT_TIMESTAMP , time) ASC , COUNT_VOTES(post_id) DESC';
			
			$query = $this->db->query($sql,array($search_key['status'],$search_key['category'],$search_key['duration']))->result_array();
			return $query;
		}
		else
		{
			$sql='SELECT * FROM post where `flag` = 0 and status = ?
				and category = ? and CURRENT_TIMESTAMP < time + INTERVAL ? DAY 
				and actual_location_id in (SELECT location_id FROM location WHERE neighbourhood = ?)
				ORDER BY datediff(CURRENT_TIMESTAMP , time) ASC , COUNT_VOTES(post_id) DESC';
			
			$query = $this->db->query($sql,array($search_key['status'],$search_key['category'],$search_key['duration'],$search_key['location']))->result_array();
			return $query;
		}
		
		/**
		SEARCH CONDITIONS:
			
			FLAG : flag = 0
		
			LOCATION : actual_location_id in (SELECT location_id FROM location WHERE neighbourhood = ?)

			CATEGORY : category = ?

			DURATION : CURRENT_TIMESTAMP < time + INTERVAL ? DAY

			STATUS : 	status = ?
		*/
	}
	
	
	/**
		Returns search result . include the posts filtered by sub-admin
	*/
	public function search_all_post($search_key)
	{
		/**
			Show all posts
		*/
		if($search_key['location']==="ANY" && $search_key['category']==="ANY" && $search_key['duration']==="ANY" && $search_key['status']==="ANY")
		{
			$sql='SELECT * FROM post ORDER BY datediff(CURRENT_TIMESTAMP , time) ASC , COUNT_VOTES(post_id) DESC';
			$query = $this->db->query($sql)->result_array();
			return $query;
		}
		/**
			Search by location (Neighbourhood)
		*/
		else if($search_key['category']==="ANY" && $search_key['duration']==="ANY" && $search_key['status']==="ANY")
		{
			$sql='SELECT * FROM post where actual_location_id in (SELECT location_id FROM location WHERE neighbourhood = ?)
				ORDER BY datediff(CURRENT_TIMESTAMP , time) ASC , COUNT_VOTES(post_id) DESC';
			$query = $this->db->query($sql,array($search_key['location']))->result_array();
			return $query;
		}
		/**
			Search by category
		*/
		else if($search_key['location']==="ANY" && $search_key['duration']==="ANY" && $search_key['status']==="ANY")
		{
			$sql='SELECT * FROM post where category = ? ORDER BY datediff(CURRENT_TIMESTAMP , time) ASC , COUNT_VOTES(post_id) DESC';
			$query = $this->db->query($sql,array($search_key['category']))->result_array();
			return $query;
		}
		/**
			Search by duration
		*/
		else if($search_key['location']==="ANY" && $search_key['category']==="ANY" && $search_key['status']==="ANY")
		{
			$sql='SELECT * FROM post where CURRENT_TIMESTAMP < time + INTERVAL ? DAY 
				ORDER BY datediff(CURRENT_TIMESTAMP , time) ASC , COUNT_VOTES(post_id) DESC';
			$query = $this->db->query($sql,array($search_key['duration']))->result_array();
			return $query;
		}
		/**
			Search by duration
		*/
		else if($search_key['location']==="ANY" && $search_key['duration']==="ANY" && $search_key['category']==="ANY")
		{
			$sql='SELECT * FROM post where status = ? ORDER BY datediff(CURRENT_TIMESTAMP , time) ASC , COUNT_VOTES(post_id) DESC';
			$query = $this->db->query($sql,array($search_key['status']))->result_array();
			return $query;
		}
		/**
			Search by location & category
		*/
		else if($search_key['duration']==="ANY" && $search_key['status']==="ANY")
		{
			$sql='SELECT * FROM post where actual_location_id in (SELECT location_id FROM location WHERE neighbourhood = ?) and category = ? 
				ORDER BY datediff(CURRENT_TIMESTAMP , time) ASC , COUNT_VOTES(post_id) DESC';
			$query = $this->db->query($sql,array($search_key['location'],$search_key['category']))->result_array();
			return $query;
		}
		/**
			Search by location & duration
		*/
		else if($search_key['category']==="ANY" && $search_key['status']==="ANY")
		{
			$sql='SELECT * FROM post where actual_location_id in 
				(SELECT location_id FROM location WHERE neighbourhood = ?)
				and CURRENT_TIMESTAMP < time + INTERVAL ? DAY 
				ORDER BY datediff(CURRENT_TIMESTAMP , time) ASC , COUNT_VOTES(post_id) DESC';

			$query = $this->db->query($sql,array($search_key['location'],$search_key['duration']))->result_array();
			return $query;
		}
		/**
			Search by location & status
		*/
		else if($search_key['category']==="ANY" && $search_key['duration']==="ANY")
		{
			$sql='SELECT * FROM post where status = ? 
				and actual_location_id in (SELECT location_id FROM location WHERE neighbourhood = ?)
				ORDER BY datediff(CURRENT_TIMESTAMP , time) ASC , COUNT_VOTES(post_id) DESC';
			$query = $this->db->query($sql,array($search_key['status'],$search_key['location']))->result_array();
			return $query;
		}
		/**
			search by category & duration
		*/
		else if($search_key['location']==="ANY" && $search_key['status']==="ANY")
		{
			$sql='SELECT * FROM post where category = ? and CURRENT_TIMESTAMP < time + INTERVAL ? DAY 
				ORDER BY datediff(CURRENT_TIMESTAMP , time) ASC , COUNT_VOTES(post_id) DESC';
			
			$query = $this->db->query($sql,array($search_key['category'],$search_key['duration']))->result_array();
			return $query;
		}
		/**
			search by category & status
		*/
		else if($search_key['location']==="ANY" && $search_key['duration']==="ANY")
		{
			$sql='SELECT * FROM post where category = ? and status = ? 
				ORDER BY datediff(CURRENT_TIMESTAMP , time) ASC , COUNT_VOTES(post_id) DESC';
			$query = $this->db->query($sql,array($search_key['category'],$search_key['status']))->result_array();
			return $query;
		}
		/**
			search by duration & status
		*/
		else if($search_key['location']==="ANY" && $search_key['category']==="ANY")
		{
			$sql='SELECT * FROM post where CURRENT_TIMESTAMP < time + INTERVAL ? DAY and status = ? 
				ORDER BY datediff(CURRENT_TIMESTAMP , time) ASC , COUNT_VOTES(post_id) DESC';
			$query = $this->db->query($sql,array($search_key['duration'],$search_key['status']))->result_array();
			return $query;
		}
		/**
			Search by duration , category & location
		*/
		else if($search_key['status']==="ANY")
		{
			$sql='SELECT * FROM post where CURRENT_TIMESTAMP < time + INTERVAL ? DAY
				and category = ? and actual_location_id in (SELECT location_id FROM location WHERE neighbourhood = ?) 
				ORDER BY datediff(CURRENT_TIMESTAMP , time) ASC , COUNT_VOTES(post_id) DESC';
			
			$query = $this->db->query($sql,array($search_key['duration'],$search_key['category'],$search_key['location']))->result_array();
			return $query;
		}
		/**
			Search by status , category & location
		*/
		else if($search_key['duration']==="ANY")
		{
			$sql='SELECT * FROM post where status = ?
				and category = ? and actual_location_id in (SELECT location_id FROM location WHERE neighbourhood = ?) 
				ORDER BY datediff(CURRENT_TIMESTAMP , time) ASC , COUNT_VOTES(post_id) DESC';
			
			$query = $this->db->query($sql,array($search_key['status'],$search_key['category'],$search_key['location']))->result_array();
			return $query;
		}
		/**
			Search by duration , category & status
		*/
		else if($search_key['location']==="ANY")
		{
			$sql='SELECT * FROM post where status = ?
				and category = ? and CURRENT_TIMESTAMP < time + INTERVAL ? DAY 
				ORDER BY datediff(CURRENT_TIMESTAMP , time) ASC , COUNT_VOTES(post_id) DESC';
			
			$query = $this->db->query($sql,array($search_key['status'],$search_key['category'],$search_key['duration']))->result_array();
			return $query;
		}
		else
		{
			$sql='SELECT * FROM post where status = ?
				and category = ? and CURRENT_TIMESTAMP < time + INTERVAL ? DAY 
				and actual_location_id in (SELECT location_id FROM location WHERE neighbourhood = ?)
				ORDER BY datediff(CURRENT_TIMESTAMP , time) ASC , COUNT_VOTES(post_id) DESC';
			
			$query = $this->db->query($sql,array($search_key['status'],$search_key['category'],$search_key['duration'],$search_key['location']))->result_array();
			return $query;
		}
		
		/**
		SEARCH CONDITIONS:
		
			LOCATION : actual_location_id in (SELECT location_id FROM location WHERE neighbourhood = ?)

			CATEGORY : category = ?

			DURATION : CURRENT_TIMESTAMP < time + INTERVAL ? DAY

			STATUS : 	status = ?
		*/
	}
	
	public function get_loginInfo($data)	//DONE
	{
		$sql='SELECT * FROM admin where `admin_name` = ? and `password` = ?';
		$query = $this->db->query($sql,array($data['admin_name'],$data['password']));
		return $query;
	}
	
	public function getAdminId($name)
	{
		$sql = "SELECT `admin_id` FROM `admin` WHERE `admin_name`=?";
		$query = $this->db->query($sql,array($name))->row_array();
		return $query['admin_id'];
	}
	
	public function getSubAdminList()
	{
		$sql = "SELECT `id`, `name` FROM `subadmin`";
		$query = $this->db->query($sql)->result_array();
		return $query;
	}
	
	public function deleteSubAdmin($id)
	{
		$sql = "DELETE FROM `subadmin` WHERE `id` = ?";
		$query = $this->db->query($sql,array($id));
	}
	
	public function addSubAdmin($name,$password)
	{
		$sql = "INSERT INTO `subadmin`(`name`, `password`) VALUES (?,?)";
		$query = $this->db->query($sql,array($name,$password));
	}

}

?>