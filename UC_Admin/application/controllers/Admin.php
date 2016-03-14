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
	
	public function take_action($post_id)
	{
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
		else if($action==1)
		{
			//Exponential Function Later
			$change= 5;
		}
		else if($action==2)
		{
			$change= -3;
		}
		else if($action==3)
		{
			//DO NOTHING
		}
		
		$this->post_model->update_rating_change($post_id,$change);
		
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