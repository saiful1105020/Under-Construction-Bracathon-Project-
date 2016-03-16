<?php

/**
*	LAST MODIFIED : 29-06-2015 04:02 PM
*/

defined('BASEPATH') OR exit('No direct script access allowed');

class Admin extends CI_Controller {
	  
	 public function __construct()
     {
          parent::__construct();
		  
		  /**
		  *	Load Libraries , Models and Helpers
		  */
		  
          $this->load->library('session');
          $this->load->helper('form');
          $this->load->helper('url');
          $this->load->helper('html');
		  $this->load->library('form_validation');
		  
		if(!isset($_SESSION["admin_name"]))
		{
			redirect('/home', 'refresh');
		}
		  
		//load models
		$this->load->model('admin_model');
		$this->load->model('post_model');
		//$data['current_nav']='home';
		//$this->load->view('templates/header',$data);
		
		$this->load->view('templates/header2');
     }
	 
	/**
	*	[ADMIN HOME PAGE]
	*/
	
	public function index()			
	{
		redirect('admin/search','refresh');
	}
	
	public function search()
	{
		if(!isset($_POST['location_select']))
		{
			$data['is_set']=false;
			$data['n_loc']=$this->admin_model->get_all_locations();
			
			$this->load->view('adminhome',$data);
		}
		else
		{
			$data['n_loc']=$this->admin_model->get_all_locations();
			
			$search_key=array();
			
			if(isset($_POST['location_select'])) $search_key['location']=$_POST['location_select'];
			else $search_key['location']="ANY";
			
			if(isset($_POST['category_select'])) $search_key['category']=$_POST['category_select'];
			else $search_key['category']="ANY";
			
			if(isset($_POST['duration_select'])) $search_key['duration']=$_POST['duration_select'];
			else $search_key['duration']="ANY";
			
			if(isset($_POST['status_select'])) $search_key['status']=$_POST['status_select'];
			else $search_key['status']="ANY";
			
			//load last 20 posts using the search_key combination
			$data['is_set']=true;
			$posts=$this->admin_model->search_post($search_key);
			$data['posts']=array();
			foreach($posts as $p)
			{
				$post=$p;
				$temp=$this->post_model->get_vote_count($p['post_id']);
				$post['up_votes']=$temp['upvotes'];
				$post['down_votes']=$temp['downvotes'];
				$post['user_name']=$this->post_model->get_user_name($p['user_id']);
				$post['user_rating']=$this->post_model->get_current_rating($post['user_name']);
				
				$post['location']=$this->post_model->get_location($p['actual_location_id']);
				
				array_push($data['posts'],$post);
			}
			$this->load->view('adminhome',$data);
			//print_r($data);
		}
	}
	
	public function take_action($post_id) //0-> PENDING, 1-> VERIFIED, 2-> REJECTED, 3-> SOLVED
	{
		//PREVIOUS STATE MUST BE "PENDING"
		
		if(isset($_POST['action'])) $action = $_POST['action'];
		else redirect('admin/search','refresh');
		
		//CHANGE STATUS OF THE POST
		$this->post_model->update_post_status($post_id,$action);
		
		//CALCULATE RATING CHANGE
		$change=0;
		if($action==0)
		{
			//DO NOTHING
		}
		// VERIFY
		else if($action==1)
		{
			//Keep it simple
			$change= 10;
		}
		//	REJECT
		else if($action==2)
		{
			$change= -7;
		}
		//	SOLVED
		else if($action==3)
		{
			$change= 15;
		}
		$this->post_model->update_rating_change($post_id,$change,$action);
		
		//USER_ID <- GET USER_ID
		$user_id = $this->post_model->get_poster_id($post_id);
		
		/**
		//SUSPENSION CHECK #CORNER CASE - VER_DATE = NULL
		
		
		//VER_DATE <- LAST DATE OF VERIFICATION / SOLVE (datetime)
		$ver_date = $this->post_model->get_last_ver_date($user_id);
		
		//POST_DATE <-	DATE OF THE POST (datetime)
		$post_date = $this->post_model->get_post_time($post_id);
		
		//IF POST_DATE < VER_DATE : DO NOTHING
		//ELSE
		
		if($post_date>=$ver_date)
		{
			//IF PENDING : DO NOTHING
			//ELSE IF REJECTED : COUNT <- COUNT+1
			if($action==2)
			{
				//if post was made before suspension, do nothing
				//else increment
				$this->post_model->inc_count($user_id);
			}
			//ELSE
			else if($action==1 || $action==3)
			{
				//if post was made before suspension,
				if($post_date<$suspension_date)
				{
					date_default_timezone_set('Asia/Dhaka');
					$current_date = date('Y-m-d H:i:s');
					
					$this->post_model->update_count($user_id,$suspension_date,$current_date);
				}
				//else
					//COUNT <- FIND NUMBER OF CONSECUTIVE REJECTED POSTS WITHIN (POST_DATE , NOW)
					date_default_timezone_set('Asia/Dhaka');
					$current_date = date('Y-m-d H:i:s');
					
					$this->post_model->update_count($user_id,$post_date,$current_date);
			}
			
			//IF COUNT>=5 ADD SUSPENSION
			$result = $this->post_model->get_suspension_data($user_id);
			if($result['count']>=5)
			{
				TODO	: DO DATABASE OPERATIONS FOR SUSPENSION
				
			}
		}	
		*/

		
		$rating = $this->post_model->get_user_rating($user_id);
		if($rating<=350)
		{
			//SUSPEND USER
			$this->post_model->suspend_user($user_id);
		}
		
		$data['success']=true;
		$data['success_message']="Status and user rating updated successfully";
		$this->load->view('status_message',$data);
	}
	
	/**
	*	Admin Logs out
	*/
	
	public function logout()	
	{
		$this->session->sess_destroy();	//!Stop Session 
		
		/**
		*Redirect To Homepage
		*/
		
		redirect('/home', 'refresh');
	}
}