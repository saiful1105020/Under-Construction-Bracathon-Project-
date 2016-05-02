<?php
defined('BASEPATH') OR exit('No direct script access allowed');

class Home extends CI_Controller {
	 
	public function __construct()		//DONE
	{
          parent::__construct();
		  
		//$this->load->library('session');
		$this->load->helper('form');
		$this->load->helper('url');
		$this->load->helper('html');
		$this->load->library('form_validation');
		  
		$this->load->model('post_model');
	}
	 
	public function index()
	{
		
	}
	
	
	public function is_verified()
	{
		
	}
	
	public function login()	
	{
		
		$email = $_GET['email'];
		$password = md5($_GET['password']);
		
		$jsonData = array();
		
		$data=$this->post_model->get_loginInfo($email,$password);
		$jsonData['userName'] = $data['userName'];
		$jsonData['userId'] = $data['userId'];
		$jsonData['isVerified'] = $data['isVerified'];
		echo json_encode($jsonData);
	}
	
	
	
	//
	//working ...
	//
	public function register()
	{
		$data['user_name'] = trim($_GET['userName']);
		$data['email'] = trim($_GET['email']);
		$data['password'] = md5($_GET['password']);
		
		$json_data = array();
		
		$json_data['status'] = $this->post_model->is_duplicate($data['email']);
		
		
		$data['user_rating']=500;
		$data['is_verified']=0;
		$data['is_suspended']=0;
		
		//GENERATE A VERIFICATION CODE AND SAVE IT TO DATABASE
		//$random_hash = md5(uniqid(rand(), true));
		$random_hash = substr(md5(uniqid(rand(), true)), 6, 6);
		
		$data['ver_code'] = $random_hash;
		
		
		
		//SAVE TO SERVER
		$json_data['userId'] = $this->post_model->register($data);
		echo json_encode($json_data);
		
		
		//send_mail();
		
	}
	
	
	
	public function verify_registration()
	{
		$user_id = $_GET['userId'];
		$ver_code_input = $_GET['verCode'];
		$ver_code = $this->post_model->get_verification_code($user_id);
		$json_data = array();
		if($ver_code_input === $ver_code)
		{
			$json_data['status'] = 1;
			$this->post_model->verify($user_id);
		}
		else $json_data['status']=0;
		echo json_encode($json_data);
		
		//IF EQUALS, VERIFIED. INSERT INTO USER DATABASE; RETURN 1
		//ELSE , RETURN 0
	}
	
	
	public function test()
	{
		$this->post_model->get_all_post(12);
	}
	
	public function send_mail()
	{
		//$json_data=array();
		//$text = phpinfo();
		// the message
		$msg = "This is a test";
		//echo json_encode($text);

		// use wordwrap() if lines are longer than 70 characters
		//$msg = wordwrap($msg,70);

		// send email
		mail("saiful_buet2011@yahoo.com","PHP Test",$msg,"From: saiful.11722@gmail.com");
		
		//echo json_encode($text);
	}
	
	public function getAllPosts()
	{
		/**
			TEST
		*/
		
		$lat = $_GET['lat'];
		$lon = $_GET['lon'];
		
		$location_id=$this->post_model->get_location_id($lat,$lon);
		
		$result=$this->post_model->get_all_post($location_id);
		
		$jsonData['posts']=array();
		
		foreach($result as $r)
		{
			
			$post['postId']=$r['post_id'];
			$post['category']=$r['category'];
			$post['timeOfPost']=$r['time'];
			$post['informalLocation']=$r['informal_location'];
			$post['problemDescription']=$r['text'];
			$post['image']=base64_encode($r['image']);
			
			$temp=$this->post_model->get_location($r['actual_location_id']);
			$post['formalLocation']=$temp['neighbourhood'];
			
			///$post['status']=$r['status'];
			///$post['rating_change']=$r['rating_change'];
			
			$post['userName']=$r['user_name'];
			$post['userRating']=$r['user_rating'];
			
			/**
				Find upvote and downvote counts
			*/
			$temp = $this->post_model->get_vote_count($post['postId']);
			$post['upCount']=$temp['upvotes'];
			$post['downCount']=$temp['downvotes'];
			
			/**
				Find user ids' who already have voted for the post
			*/
			$tmp = $this->post_model->get_voters($post['postId']);
			$post['upVoters']=$tmp['up_voters'];
			$post['downVoters']=$tmp['down_voters'];
			
			array_push($jsonData['posts'],$post);
		}
		
		echo json_encode($jsonData);
	}
	
	/**
		Send the list of existing categories
	*/
	public function getCategoryList()
	{
		$jsonData['catList'] = array();
		
		$temp = $this->post_model->get_category_list();
		foreach($temp as $t)
		{	
			array_push($jsonData['catList'],$t);
		}
		
		echo json_encode($jsonData);
	}
	
	/**
		Insert a user suggestion for new category.
		if any duplicate exists, count++
		otherwise, insert with count 1
	*/
	public function insertCatSuggestion()
	{
		$newCat = $_GET['category'];
		//$jsonData['str'] = $this->mapString("Hi T here");
		//echo json_encode($jsonData);
		//check duplicate
		$duplicate = 0;
		$duplicateId = -1;
		//$jsonData['catList'] = array();
		
		
		$temp = $this->post_model->get_suggested_category_list();
		foreach($temp as $t)
		{
			$catName = $t['name'];
			
			//array_push($jsonData['catList'],$this->mapString($catName));
			
			if($this->mapString($newCat) === $this->mapString($catName)) 
			{
				//array_push($jsonData['catList'],$this->mapString($catName));
				$duplicate = 1;
				$duplicateId = $t['id'];
				break;
			}
		}
		
		if($duplicate == 1)
		{
			//increment count
			$this->post_model->inc_suggestion_count($duplicateId);
		}
		else
		{
			$this->post_model->insert_cat_sugegstion($newCat);
		}
		
		//$jsonData['dup'] = $duplicate;
		//echo json_encode($jsonData);
	}
	
	/**
		Open Dustbean = open dustbean = openDustbean = o pen dus t bean = OPENDUSTBEAN
		Remove all whitespace characters
	*/
	public function mapString($strIn)
	{
		//$strIn = $_GET['string'];
		
		$str = "";
		$length = strlen($strIn);
		
		for($i=0;$i<$length;$i++)
		{
			if(('a'<=$strIn[$i] and $strIn[$i]<='z') or ('A'<=$strIn[$i] and $strIn[$i]<='Z') or ('0'<=$strIn[$i] and $strIn[$i]<='9')) $str=$str.$strIn[$i];
		}
		
		$str = strtoupper($str);
		
		//$jsonData['str'] = $str;
		//echo json_encode($jsonData);
		return $str;
	}
	
	public function submitVote()
	{
		$user_id = $_POST['userId'];
		
		$post_id = $_POST['postId'];
		$vote_type = $_POST['voteType'];
		
		$this->post_model->submit_vote($user_id,$post_id,$vote_type);
	}
	
	public function insertPost()
	{
		//insert location and get location id
		
		$loc=array();
		
		if(isset($_POST['latitude'])) $loc['lat']=$_POST['latitude'];
		else $loc['lat']='';
		
		if(isset($_POST['longitude'])) $loc['lon']=$_POST['longitude'];
		else $loc['lon']='';
		
		if(isset($_POST['streetNo'])) $loc['street_number']=$_POST['streetNo'];
		else $loc['street_number']='';
		if(isset($_POST['route'])) $loc['route']=$_POST['route'];
		else $loc['route']='';
		if(isset($_POST['neighborhood'])) $loc['neighbourhood']=$_POST['neighborhood'];
		else $loc['neighbourhood']='';
		if(isset($_POST['sublocality'])) $loc['sublocality']=$_POST['sublocality'];
		else $loc['sublocality']='';
		if(isset($_POST['locality'])) $loc['locality']=$_POST['locality'];
		else $loc['locality']='';
		
		$location_id = $this->post_model->insert_location($loc);
		
		$loc['id']=$location_id;
		
		$post=array();
		
		$post['user_id'] = $_POST['userId'];

		/**
			if category doesn't exist, use Others
			Not Tested
		*/
		if(isset($_POST['category']))
		{
			$catExist = $this->post_model->existCategory($_POST['category']);
			if($catExist == 1)
			{
				$post['category']=$_POST['category'];
			}
			else
			{
				$post['category']=-1;
			}
		}
		else $post['category']='';
		
		
		if(isset($_POST['image']))$post['image']=base64_decode($_POST['image']);
		else $post['image']='';
		
		if(isset($_POST['time']))$post['time']=$_POST['time'];
		else $post['time']='';
		
		if(isset($_POST['informalLocation'])) $post['informal_location']=$_POST['informalLocation'];
		else $post['informal_location']='';
		
		if(isset($_POST['problemDescription']))$post['text']=$_POST['problemDescription'];
		else $post['text']='';
		
		$post['actual_location_id']=$location_id;
		$post['status']=0;
		$post['rating_change']=0;
		
		$this->post_model->insert_post($post);
		
		
		//Just for debug, no need to comment out. Let it stay as it is
		$debug['post_inserted']="OK";
		echo json_encode($debug);
		
	}

	public function getDashboardGraphData($user_id)
	{
		$result=$this->post_model->get_last_10_user_post($user_id);
		
		$jsonData=array();
		
		$post=array();
		$post['time']=$this->post_model->current_time();
		$post['ratingChange']="0";
		array_push($jsonData,$post);
		
		foreach($result as $r)
		{
			$post=array();
			$post['time']=$r['time'];
			$post['ratingChange']=$r['rating_change'];
			array_push($jsonData,$post);
		}
		
		return $jsonData;
	}
	
	public function getSuggestions()
	{
		$lat=$_GET['lat'];
		//else $lat='';
		
		$lon=$_GET['lon'];
		//else $lon='';
	
		$time=$_GET['time'];
		//else $time='';
		
		$cat=$_GET['cat'];
		//else $cat='';
		
		$jsonData['posts'] =$this->post_model->get_suggestions($lat,$lon,$time,$cat);
		
		/*
		$jsonData['posts'] = array();
		
		foreach($result as $r)
		{
			$post['userName']=$r['userName'];
			$post['cat']=$r['cat'];
			//$post['image'] = $r['img'];
			array_push($jsonData['posts'],$post);
		}
		*/
		
		
		
		//$posts;
		
		echo json_encode($jsonData);
	}
	
	public function getUserPosts()
	{
		$user_id = $_GET['userId'];
		$result=$this->post_model->get_user_posts($user_id);
		
		$jsonData['posts']=array();
		
		foreach($result as $r)
		{
			$post['category']=$r['category'];
			$post['timeStamp']=$r['time'];
			$post['locationDescription']=$r['informal_location'];
			$post['problemDescription']=$r['text'];
			///$post['image']=base64_encode($r['image']);
			$temp=$this->post_model->get_location($r['actual_location_id']);
			$post['exactLocation']=$temp['neighbourhood'];
			$post['state']=$r['status'];
			$post['ratingChanged']=$r['rating_change'];
			
			$temp = $this->post_model->get_vote_count($r['post_id']);
			$post['upVote']=$temp['upvotes'];
			$post['downVote']=$temp['downvotes'];
			array_push($jsonData['posts'],$post);
		}
		
		//$jsonData['userRating']=$this->post_model->get_current_rating($user_id);
		
		//$jsonData['rating']=$this->getDashboardGraphData($user_id);
		
		echo json_encode($jsonData);
	}
	
	public function getUserRating()
	{
		$user_id = $_GET['userId'];
		
		$jsonData['userRating']=$this->post_model->get_current_rating($user_id);
		
		$jsonData['rating']=$this->getDashboardGraphData($user_id);
		
		//$jsonData['rating']=500;
		echo json_encode($jsonData);
		
	}
}
