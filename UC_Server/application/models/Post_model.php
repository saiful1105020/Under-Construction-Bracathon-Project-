<?php
class Post_model extends CI_Model 
{
	/**
		STATUS:
			0 -> PENDING
			1 -> VERIFIED
			2 -> REJECTED
			3 -> SOLVED
	*/
	public function __construct()	
	{
        $this->load->database();
	}	

	/**
		Find the most recent posts near a given location.
		--(Done)-Add this clause "where CURRENT_TIMESTAMP < time + INTERVAL ? DAY" inside the query
	*/
	public function get_all_post($location_id)
	{
		$sql='select p.*,u.user_id,u.user_name,u.user_rating from post p, user u where p.actual_location_id = ? and p.status = 0 and p.user_id=u.user_id order by p.time desc';
		$query=$this->db->query($sql,$location_id)->result_array();
		return $query;
	}
	
	/*
	--Unnecessary now
	public function get_all_post_backup($location_id)
	{
		$sql='select p.*,u.user_name,u.user_rating from post p, user u where p.actual_location_id = ? and p.status = 0 and p.user_id=u.user_id order by p.time desc';
		$query=$this->db->query($sql,$location_id)->result_array();
		return $query;
	}
	*/
	
	/**
		--Unnecessary now
		use user_id instead of user_name
	*/
	/*
	public function get_last_10_user_post($user_name)
	{
		$sql = 'SELECT p.time,p.rating_change FROM post p, user u WHERE u.user_name = ? and p.user_id = u.user_id ORDER BY p.time DESC LIMIT 10';
		$query=$this->db->query($sql,$user_name);
		
		$result=$query->result_array();
		
		return $result;
	}
	*/
	
	/**
		Retrieve last 10 posts(pending/verified/rejected/solved) of a user
		Used for generating rating graph and dashboard posts only. No other usage.
	*/
	public function get_last_10_user_post($user_id)
	{
		$sql = 'SELECT p.time,p.rating_change FROM post p WHERE p.user_id = ? ORDER BY p.time DESC LIMIT 10';
		$query=$this->db->query($sql,$user_id);
		
		$result=$query->result_array();
		
		return $result;
	}
	
	public function current_time()
	{
		$sql="SELECT current_timestamp() as curTime";
		$query=$this->db->query($sql);
		$result=$query->row_array();
		
		return $result['curTime'];
	}
	
	
	/**
		Used for login process.
		Input: email-id and password
		Output: userId, userName, isVerified
		
		userId: id of the user if found in database, if not found, return 0
		userName: If not found, return ""
		isVerified: 0 when the account is not verified(need to go through verification process), 1 represents a verified account.
	*/
	public function get_loginInfo($email,$password)
	{
		$sql="SELECT user_id as userId,user_name as userName,is_verified as isVerified FROM user WHERE email = ? and password = ?";
		$query = $this->db->query($sql,array($email,$password));
		
		$data=array();	
		if($query->num_rows()==0)
		{
			$data['userId']=0;
			$data['userName']="";
			$data['isVerified']=0;
		}
		else
		{
			$temp = $query->row_array();
			$data['userId']=$temp['userId'];
			$data['userName']=$temp['userName'];
			$data['isVerified']=$temp['isVerified'];
		}
		return $data;
	}
	
	/**
		Mark a user as verified.
	*/
	public function verify($user_id)
	{
		$sql = "UPDATE user SET is_verified = 1 WHERE user_id = ?";
		$query = $this->db->query($sql,array($user_id));
	}
	
	/**
		Return 0 when, the email is already taken/used
		Return 1 if the email is not used
	*/
	public function is_email_available($email)
	{
		$sql="SELECT user_id FROM user WHERE email = ? ";
		$query = $this->db->query($sql,array($email))->row_array();
		if($query['user_id']==NULL)
		{
			return 1;
		}
		else
		{
			return 0;
		}
	}
	
	/**
		Tried to detect similar posts based on location(lat,long), time of post and category.
		There is a huge room for improvement here.
	*/
	public function get_suggestions($lat,$lon,$time,$cat)
	{
		$sql = 'SELECT u.user_id as user_id,u.user_rating as user_rating,u.user_name as user_name, p.*,
				l.street_number as streetNo,l.route as route,l.neighbourhood as neighbourhood,l.sublocality as sublocality,l.locality as locality
				FROM `user` u,`post` p, `location` l 
				WHERE u.user_id = p.user_id AND l.location_id = p.actual_location_id AND p.category = ? 
				AND abs(l.`lat` - ?) <= 0.0004 AND abs(l.`lon` - ?) <=0.0004
				AND abs(datediff(p.`time`,?)) <=7 AND p.status =0';
				
		$result = $this->db->query($sql,array($cat,$lat,$lon,$time))->result_array();
		
		return $result;
	}
	
	/**
		Returns user_id when the registration is complete
	*/
	public function register($data)
	{
		$sql = "INSERT INTO `user`(`user_name`, `email`, `user_rating`, `password`, 
				`is_verified`, `ver_code`, `is_suspended`)
				VALUES (?,?,?,?,?,?,?)";
		
		$query= $this->db->query($sql,array($data['user_name'],$data['email'],
				$data['user_rating'],$data['password'],$data['is_verified'],$data['ver_code'],$data['is_suspended']));
				
		$sql = "SELECT `user_id` FROM user WHERE `email` = ?";
		$query = $this->db->query($sql,array($data['email']))->row_array();
		return $query['user_id'];
	}
	
	/**
		Returns the verification code for a user
	*/
	public function get_verification_code($user_id)
	{
		$sql = "SELECT `ver_code` FROM `user` WHERE `user_id` = ? ";
		$query = $this->db->query($sql,array($user_id))->row_array();
		return $query['ver_code'];
	}
	
	
	/*
	unnecessary now
	public function get_user_posts($user_name)
	{
		$sql = 'select p.* from post p, user u where u.user_name = ? and p.user_id=u.user_id order by p.time desc';
		$query=$this->db->query($sql,$user_name)->result_array();
		return $query;
	}
	*/
	
	/**
		Returns posts of a user (limitted to 20 posts only. You may change it if you need).
	*/
	public function get_user_posts($user_id)
	{
		$sql = 'select p.* from post p where p.user_id=? order by p.time desc limit 20';
		$query=$this->db->query($sql,$user_id)->result_array();
		return $query;
	}
	
	/**
		Count total upvotes and downvotes for a post.
	*/
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
	
	/**
		Return user_id against a user_name
		--no usage of this function. We should not use it as user_name may not be unique
	
	public function get_user_id($user_name)
	{
		$sql="SELECT user_id FROM user WHERE user_name = ? ";
		$query=$this->db->query($sql,$user_name)->row_array();
		return $query['user_id'];
	}
	*/
	
	/**
		Returns a list of user_id's who either upvoted or downvoted a post
	*/
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
	
	/**
		If the user has already voted for this post, just change (update) the vote type
		Otherwise, insert the new vote
	
		VOTE_TYPE:
			1   -> upvote
			-1	-> downvote
	*/
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
	
	/**
		Insert a new post
	*/
	public function insert_post($post)
	{
		$sql='INSERT INTO `post`(`user_id`, `category`, `image`,`time`, `informal_location`, `text`, `actual_location_id`, `status`, `rating_change`) VALUES (?,?,?,?,?,?,?,?,?)';
		$query=$this->db->query($sql,array($post['user_id'], $post['category'], $post['image'], $post['time'], $post['informal_location'], $post['text'], $post['actual_location_id'], $post['status'], $post['rating_change']));
	}
	
	/**
		Returns current total rating of a user
	*/
	public function get_current_rating($user_id)
	{
		$sql='SELECT user_rating FROM user WHERE user_id = ? ';
		$query=$this->db->query($sql,array($user_id))->row_array();
		return $query['user_rating'];
	}
	
	/**
		This function may be used to perfectly calculate distance between two GEO co-ordinates.
		To do that, we need to implement a mysql function
	*/
	public function distance($lat1, $lon1, $lat2, $lon2) {

	  $theta = $lon1 - $lon2;
	  $dist = sin(deg2rad($lat1)) * sin(deg2rad($lat2)) +  cos(deg2rad($lat1)) * cos(deg2rad($lat2)) * cos(deg2rad($theta));
	  $dist = acos($dist);
	  $dist = rad2deg($dist);
	  $miles = $dist * 60 * 1.1515;
	  
	  return $miles * 1609.344;
	}
	
	/**
		Try to match an existing location in database with user's location.
		If found, return location_id
		Otherwise, return -1 to indicate no found
	*/
	public function get_location_id($lat,$lon)
	{
		$sql='SELECT location_id FROM location WHERE abs(`lat` - ?) <= 0.002 AND abs(`lon` - ?) <=0.002';
		$query=$this->db->query($sql,array($lat,$lon));
		
		if($query->num_rows() > 0)
		{
			$t = $query->row_array();
			return $t['location_id'];
		}
		else return -1;
	}
	
	/**
		Insert(?) location and return location_id
		
		First, try to match an existing location in database
		If found, no need to insert again -- just return the location_id
		Otherwise, Just return the location_id matched
	*/
	public function insert_location($data)
	{
		$sql='SELECT location_id FROM location WHERE abs(`lat` - ?) <= 0.002 AND abs(`lon` - ?) <=0.002';
		$query=$this->db->query($sql,array($data['lat'],$data['lon']));
		
		$loc = array();
		
		if($query->num_rows() == 0)
		{
			$s='INSERT INTO location (`lat`, `lon`, `street_number`, `route`, `neighbourhood`, `sublocality`, `locality`) VALUES (?,?,?,?,?,?,?)';
			$q=$this->db->query($s,array($data['lat'],$data['lon'],$data['street_number'],
			$data['route'],$data['neighbourhood'],$data['sublocality'],$data['locality']));
			
			$s='SELECT location_id FROM location WHERE abs(`lat` - ?) <= 0.002 AND abs(`lon` - ?) <=0.002';
			$q=$this->db->query($s,array($data['lat'],$data['lon']))->row_array();
			return $q['location_id'];
		}
		else
		{
			$result=$query->row_array();
			return $result['location_id'];
		}
	}
	
	/**
		Returns location details for a location_id
	*/
	public function get_location($location_id)
	{
		$sql='SELECT * FROM location WHERE location_id = ?';
		$query=$this->db->query($sql,array($location_id))->row_array();
		return $query;
	}
	
	/**
		Returns the list of category
	*/
	public function get_category_list()
	{
		$sql='SELECT * FROM category';
		$query=$this->db->query($sql)->result_array();
		return $query;
	}
	
	/**
		Returns the list of category name suggested by the users
	*/
	public function get_suggested_category_list()
	{
		$sql='SELECT * FROM suggestedCategory';
		$query=$this->db->query($sql)->result_array();
		return $query;
	}
	
	/**
		Increment count field of a suggested category
	*/
	public function inc_suggestion_count($duplicateId)
	{
		$sql = 'UPDATE `suggestedcategory` SET `count`=`count` WHERE `id`= ?';
		$query=$this->db->query($sql,array($duplicateId));
	}
	
	/**
		Insert a suggested category
	*/
	public function insert_cat_sugegstion($newCat)
	{
		$sql = 'INSERT INTO `suggestedcategory`(`name`, `count`) VALUES (?,1)';
		$query=$this->db->query($sql,array($newCat));
	}
	
	/**
		Returns 1 if the category name ($cat) matches with any of the categories in database
		Otherwise, Return 0
	*/
	public function existCategory($cat)
	{
		$sql = 'SELECT COUNT(*) as cnt FROM category WHERE `categoryId` = ?';
		$query=$this->db->query($sql,array($cat))->row_array();
		$count = $query['cnt'];
		
		if($count>0) return 1;
		else return 0;
	}

	public function maxPostID()
	{
		$sql = 'SELECT MAX(post_id) as biggest from post';
		$query= $this->db->query($sql)->row_array();

		return $query['biggest'];
	}
}
?>