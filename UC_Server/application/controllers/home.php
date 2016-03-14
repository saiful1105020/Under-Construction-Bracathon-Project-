<?php
defined('BASEPATH') OR exit('No direct script access allowed');

class Home extends CI_Controller {
	 
	 public function __construct()		//DONE
     {
          parent::__construct();
		  
		  $this->load->library('session');
          $this->load->helper('form');
          $this->load->helper('url');
          $this->load->helper('html');
		  $this->load->library('form_validation');
		  
		  $this->load->model('post_model');
     }
	 
	public function index()
	{
		
	}
	
	/*
	public function login()	
	{
		
		$user_name = $_GET['userName'];
		$password = md5($_GET['password']);
		
		$jsonData = array();
		
		$jsonData['user_name']=$user_name;
		$jsonData['user_id']=$this->post_model->get_loginInfo($user_name,$password);
		
		echo json_encode($jsonData);
	}
	*/
	
	/*
	public function checkDuplicateUserName()
	{
		$user_name = trim($_POST['userName']);
		
		$jsonData=array();
		$jsonData['duplicate']=$this->post_model->is_duplicate($user_name);
		echo json_encode($jsonData);
	}
	*/
	
	/*
	public function register()
	{
		$data['user_name'] = trim($_POST['userName']);
		$data['password'] = $_POST['password'];
		$data['user_rating']=500;
		
		$this->post_model->register($data);
	}
	*/
	
	public function test()
	{
		$this->post_model->get_all_post(12);
	}
	
	public function getAllPosts()
	{
		/**
			Need to fix.
			input should be <lat,lon> ; 
			location_id must be determined from Server side; rest seems OK
		*/
		$location_id=$_GET['locationId'];
		$result=$this->post_model->get_all_post($location_id);
		
		$jsonData['posts']=array();
		
		foreach($result as $r)
		{
			
			$post['postId']=$r['post_id'];
			
			///$post['user_id']=$r['user_id'];
			
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
	
	public function submitVote()
	{
		/**
			User user_id instead of user_name
		*/
		
		$user_name = $_POST['userName'];
		$user_id = $this->post_model->get_user_id($user_name);
		
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
		
		///////////////////////////////////////////////////////////////////////////////////
		//								OLD CODE										///
		///////////////////////////////////////////////////////////////////////////////////
		
		/**
			User user_id instead of user_name  -- DONE?
		*/
		//get user_id
		/*
		if(isset($_POST['userName'])) $user_name = $_POST['userName'];
		else $user_name='';
		
		$post['user_id']=$this->post_model->get_user_id($user_name);
		*/
		
		///////////////////////////////////////////////////////////////////////////////////
		//								END OF OLD CODE									///
		///////////////////////////////////////////////////////////////////////////////////
		
		
		///////////////////////////////////////////////////////////////////////////////////
		//								NEW CODE										///
		///////////////////////////////////////////////////////////////////////////////////
		
		$user_id = $_POST['userId'];
		
		///////////////////////////////////////////////////////////////////////////////////
		//								END OF NEW CODE									///
		///////////////////////////////////////////////////////////////////////////////////
		
		if(isset($_POST['category'])) $post['category']=$_POST['category'];
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
		
		/*
		//Just for debug
		$debug['post_inserted']="OK";
		echo json_encode($debug);
		*/
	}
	
	/**
		use user_id instead of user_name
	*/
	public function getDashboardGraphData($user_id)
	{
		//$user_name=$_GET['userName'];
		
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
	
	/**
		use user_id instead of user_name
	*/
	public function getUserPosts()
	{
		/*	old code(worked)
		$user_name=$_GET['userName'];
		$result=$this->post_model->get_user_posts($user_name);
		*/
		
		//	new code
		$user_id = $_GET['userId'];
		//
		
		//TEST CODES
			//$jsonData['userId']=$user_id;
		//
		
		// new code
		$result=$this->post_model->get_user_posts($user_id);
		//
		
		$jsonData['posts']=array();
		
		foreach($result as $r)
		{
			///$post['user_id']=$r['user_id'];
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
		
		$jsonData['userRating']=$this->post_model->get_current_rating($user_id);
		
		$jsonData['rating']=$this->getDashboardGraphData($user_id);
		
		echo json_encode($jsonData);
	}
}
