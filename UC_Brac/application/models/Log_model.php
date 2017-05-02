<?php
class Log_model extends CI_Model 
{
	/**
		log_type:
			
			2 => User Reported a problem
			3 => Admin logged in
			4 => Admin updated a post status
			5 => Admin logged out
			6 => Admin added a category
			7 => Admin deleted a category
			8 => Sub-admin logged in
			9 => Sub-admin logged out
			10 => Sub-admin flagged/unflagged a post
	*/
	
	/**
		DATABASE
		--------
		table name : logs
		attributes:	same attributes used for different purposes based on log_type
			user_id : user/admin/sub-admin id
			cat_id : Category/Suggested Category id
			time : timestamp
			log_type : 1-10
			post_id : id of related post
			changed_status : PENDING(0)/VERIFIED(1)/REJECTED(2)/SOLVED(3) OR FLAGGED(1)/UNFLAGGED(0)
			
			cat_name : Name of the category/ suggested category
	*/
	
    public function __construct()	//DONE
	{
        $this->load->database();
	}
	
	public function insert_log($data)
	{
		//echo '<br><br><br>';
		//print_r($data);
		/**
			data must contain 6 attributes
		*/
		$sql = 'INSERT INTO `logs`(`user_id`, `cat_id`, `time`, `log_type`, `post_id`, `changed_status`, `cat_name`) VALUES (?,?,CURRENT_TIMESTAMP,?,?,?,?)';
		$this->db->query($sql,array($data['user_id'],$data['cat_id'],$data['log_type'],$data['post_id'],$data['changed_status'],$data['cat_name']));
	}
	
	public function get_all_logs()
	{
		$sql = 'SELECT * FROM `logs` ORDER BY time DESC';
		$query = $this->db->query($sql)->result_array();
		return $query;
	}
	
	/**
		Apparently hard-coded. need to redo
	*/
	public function get_user_name($user_id)
	{
		$sql = "SELECT `user_name` FROM `user` WHERE `user_id` = ?";
		$query = $this->db->query($sql,array($user_id))->row_array();
		return $query['user_name'];
	}
	
	public function get_user_rating($user_id)
	{	
		$sql = "SELECT `user_rating` FROM `user` WHERE `user_id` = ?";
		$query = $this->db->query($sql,array($user_id))->row_array();
		return $query['user_rating'];
	}
	
	public function get_admin_name($user_id)
	{
		$sql = "SELECT `admin_name` FROM `admin` WHERE `admin_id` = ?";
		$query = $this->db->query($sql,array($user_id))->row_array();
		return $query['admin_name'];
	}
	
	public function get_subAdmin_name($user_id)
	{
		$sql = "SELECT `name` FROM `subadmin` WHERE `id` = ?";
		$query = $this->db->query($sql,array($user_id))->row_array();
		return $query['name'];
	}
}

?>