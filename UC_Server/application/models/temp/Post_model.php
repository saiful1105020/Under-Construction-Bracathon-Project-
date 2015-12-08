<?php
class Post_model extends CI_Model 
{
	public function __construct()	//DONE
	{
        $this->load->database();
	}	

	public function get_all_post($location_id)
	{
		$sql='select p.*,u.user_name,u.user_rating from post p, user u where p.actual_location_id = ? and p.status = 0 and p.user_id=u.user_id order by p.time';
		$query=$this->db->query($sql,$location_id)->result_array();
		return $query;
	}
	
	public function get_last_10_user_post($user_name)
	{
		//$sql='select p.time,p.rating_change from post p, user u where u.user_name = ? and p.user_id=u.user_id order by p.time';
		$sql = 'SELECT p.time,p.rating_change FROM post p, user u WHERE u.user_name = ? and p.user_id = u.user_id ORDER BY p.time DESC';
		$query=$this->db->query($sql,$user_name);
		
		$result=$query->result_array();
		
		return $result;
	}
	
	public function get_user_name($user_id)
	{
		$sql="SELECT user_name FROM user WHERE user_id = ? ";
		$query=$this->db->query($sql,$user_id)->row_array();
		return $query['user_name'];
	}
	
	public function get_user_posts($user_name)
	{
		$sql = 'select p.* from post p, user u where u.user_name = ? and p.user_id=u.user_id order by p.time';
		$query=$this->db->query($sql,$user_name)->result_array();
		return $query;
	}
	
	public function get_vote_count($post_id)
	{
		$sql='SELECT COUNT(*) as upvotes FROM vote WHERE post_id=? and vote_type=1';
		$query=$this->db->query($sql,$post_id)->row_array();
		
		$data['upvotes']=$query['upvotes'];
		
		$sql='SELECT COUNT(*) as downvotes FROM vote WHERE post_id=? and vote_type=-1';
		$query=$this->db->query($sql,$post_id)->row_array();
		
		$data['downvotes']=$query['downvotes'];
		
		return $data;
	}
	
	public function get_user_id($user_name)
	{
		$sql="SELECT user_id FROM user WHERE user_name = ? ";
		$query=$this->db->query($sql,$user_name)->row_array();
		return $query['user_id'];
	}
	
	public function get_voters($post_id)
	{
		$result=array();
		$sql='SELECT user_id as userId FROM vote WHERE post_id=? AND vote_type=1';
		$query=$this->db->query($sql,$post_id)->result_array();
		$result['up_voters']=$query;
		
		$sql='SELECT user_id as userId FROM vote WHERE post_id=? AND vote_type=-1';
		$query=$this->db->query($sql,$post_id)->result_array();
		$result['down_voters']=$query;
		
		return $result;
	}
	
	public function submit_vote($user_id,$post_id,$vote_type)
	{
		$sql = 'SELECT COUNT(*) as cnt FROM vote WHERE user_id=? AND post_id=?';
		$query=$this->db->query($sql,array($user_id,$post_id))->row_array();
		
		if($query['cnt']!=0)
		{
			$sql='UPDATE `vote` SET `vote_type`=? WHERE user_id = ? AND post_id =? ';
			$query=$this->db->query($sql,array($vote_type,$user_id,$post_id));
		}
		else
		{
			$sql='INSERT INTO `vote`(`user_id`, `post_id`, `vote_type`) VALUES (?,?,?)';
			$query=$this->db->query($sql,array($user_id,$post_id,$vote_type));
		}
	}
	
	public function get_location($location_id)
	{
		$sql='SELECT * FROM location WHERE location_id = ?';
		$query=$this->db->query($sql,array($location_id))->row_array();
		return $query;
	}
	
	public function update_post_status($post_id,$status)
	{
		//echo $post_id;
		//echo $status;
		//change post status
		$sql='UPDATE `post` SET `status`=? WHERE post_id = ?';
		$query=$this->db->query($sql,array($status,$post_id));
	}
	
	public function update_rating_change($post_id,$change)
	{
		$sql='UPDATE `post` SET `rating_change`=? WHERE post_id = ?';
		$query=$this->db->query($sql,array($change,$post_id));
		
		//update user rating
		$sql='SELECT user_id FROM post WHERE post_id=?';
		$query=$this->db->query($sql,$post_id)->row_array();
		
		$user_id=$query['user_id'];
		//get current user rating
		
		$sql='SELECT user_rating FROM user WHERE user_id = ? ';
		$query=$this->db->query($sql,array($user_id))->row_array();
		$current_rating = $query['user_rating'];
		
		$current_rating+=$change;
		
		//update rating
		$sql='UPDATE user SET user_rating = ? WHERE user_id = ? ';
		$query=$this->db->query($sql,array($current_rating,$user_id));
		
	}
	
	/**
		WASIF
	*/
	public function insert_post($post)
	{
		$sql='INSERT INTO `post`(`user_id`, `category`, `image`,`time`, `informal_location`, `text`, `actual_location_id`, `status`, `rating_change`) VALUES (?,?,?,?,?,?,?,?,?)';
		$query=$this->db->query($sql,array($post['user_id'], $post['category'], $post['image'], $post['time'], $post['informal_location'], $post['text'], $post['actual_location_id'], $post['status'], $post['rating_change']));
	}
	
	public function get_current_rating($user_name)
	{
		$sql='SELECT user_rating FROM user WHERE user_name = ? ';
		$query=$this->db->query($sql,array($user_name))->row_array();
		//implement
		return $query['user_rating'];
	}
	
	//insert location and get location id
	public function insert_location($data)
	{
		$sql='SELECT location_id FROM location WHERE abs(`lat` - ?) <= 0.001 AND abs(`lon` - ?) <=0.001';
		$query=$this->db->query($sql,array($data['lat'],$data['lon']));
		
		$loc = array();
		
		if($query->num_rows() == 0)
		{
			$s='INSERT INTO location (`lat`, `lon`, `street_number`, `route`, `neighbourhood`, `sublocality`, `locality`) VALUES (?,?,?,?,?,?,?)';
			$q=$this->db->query($s,array($data['lat'],$data['lon'],$data['street_number'],$data['route'],$data['neighbourhood'],$data['sublocality'],$data['locality']));
			
			$s='SELECT location_id FROM location WHERE abs(`lat` - ?) <= 0.001 AND abs(`lon` - ?) <=0.001';
			$q=$this->db->query($s,array($data['lat'],$data['lon']))->row_array();
			return $q['location_id'];
		}
		else
		{
			$result=$query->row_array();
			return $result['location_id'];
		}
	}
}
?>