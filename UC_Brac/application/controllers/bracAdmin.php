<?php

/**
*	LAST MODIFIED : 29-06-2015 04:02 PM
*/

defined('BASEPATH') OR exit('No direct script access allowed');

class BracAdmin extends CI_Controller {
	  
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
		  
		if(!isset($_SESSION["bracAdminName"]))
		{
			redirect('/home', 'refresh');
		}
		  
		//load models
		$this->load->model('bracAdminModel');
		$this->load->model('post_model');
		$this->load->model('log_model');
		
		$this->load->view('templates/header2');
     }
	 
	public function  showLog($index)
	{
		echo '<br><br><br>';
		$temp = $this->log_model->get_all_logs();
		
		$data = array();
		$logArray = array();
		//print_r($temp);
		
		for($i=0;$i<50;$i++)
		{
			$idx = $index*50+$i;
			if($idx>=sizeof($temp)) break;

			$logArray[$i] = $temp[$idx];
			$logArray[$i]['message'] =  $this->form_string($temp[$idx]);
			
			
			
			//$data['message'] = $this->form_string($temp[$idx]);
			//echo $data['message'].'    -------    '.$temp[$idx]['time'];
			//print_r($temp[$idx]);
			//echo '<br>';
			
			
		}

		//print_r($logArray);

		$data['index'] = $index;
		$data['logData'] =$logArray;
		$this->load->view('bracHome',$data);
	}
	
	public function form_string($log)
	{
		$str = "";
		
		if($log['log_type']==2)
		{
			$str = "User ".$this->log_model->get_user_name($log['user_id'])." (".$this->log_model->get_user_rating($log['user_id']).") Reported a problem";
		}
		else if($log['log_type']==3)
		{
			$str = "Admin ".$this->log_model->get_admin_name($log['user_id'])." logged in";
		}
		else if($log['log_type']==4)
		{
			/**
				actions : 
					0 => PENDING
					1 => VERIFIED
					2 => REJECTED
					3 => SOLVED
			*/
			$tempstr = array(0=>"PENDING",1=>"VERIFIED",2=>"REJECTED",3=>"SOLVED");
			$str = "Admin ".$this->log_model->get_admin_name($log['user_id'])." marked a post as ".$tempstr[$log['changed_status']];
			
			//print_r($tempstr);
		}
		else if($log['log_type']==5)
		{
			$str = "Admin ".$this->log_model->get_admin_name($log['user_id'])." logged out";
		}
		else if($log['log_type']==6)
		{
			$str = "Admin ".$this->log_model->get_admin_name($log['user_id'])." added a new category : <b>".$log['cat_name'].'</b>';
		}
		else if($log['log_type']==7)
		{
			$str = "Admin ".$this->log_model->get_admin_name($log['user_id'])." deleted a category : <b>".$log['cat_name'].'</b>';
		}
		else if($log['log_type']==8)
		{
			$str = "Sub-Admin ".$this->log_model->get_subAdmin_name($log['user_id'])." logged in";
		}
		else if($log['log_type']==9)
		{
			$str = "Sub-Admin ".$this->log_model->get_subAdmin_name($log['user_id'])." logged out";
		}
		else if($log['log_type']==10)
		{
			$tempstr = array(0=>"UNFLAGGED",1=>"FLAGGED");
			$str = "Sub-Admin ".$this->log_model->get_subAdmin_name($log['user_id'])." marked a post as ".$tempstr[$log['changed_status']];
		}
		return $str;
	}


	public function showPost($post_id)
	{
		$p=$this->post_model->get_post_info($post_id);
		//echo '<br><br><br>';
		//print_r($p);
		
		$post = $p;
	
		$temp=$this->post_model->get_vote_count($p['post_id']);
				
		$post['up_votes']=$temp['upvotes'];
		$post['down_votes']=$temp['downvotes'];
				
		$post['user_name']=$this->post_model->get_user_name($p['user_id']);
				
		$post['user_rating']=$this->post_model->get_current_rating($p['user_id']);
				
		$post['cat_name'] = $this->post_model->get_category_name($p['category']);
				
		$post['location']=$this->post_model->get_location($p['actual_location_id']);
		
		//print_r($post);

		$data['post'] = $post;

		$this->load->view('showPost',$data);
	}
	 
	/**
	*	[ADMIN HOME PAGE]
	*/
	
	public function index()			
	{
		redirect('bracAdmin/showLog/0','refresh');
	}
	

	
	//--------------------------------------------------------------------------------------//
	//--------------------------------------------------------------------------------------//
	
	/**
	*	Admin Logs out
	*/
	
	public function logout()	
	{
		//$logData = array();
		
		//$logData['user_id']=$_SESSION["bracAdmin_id"];
		
		$this->session->sess_destroy();	//!Stop Session 
		
		/**
		*Redirect To Homepage
		*/
		
		redirect('/home', 'refresh');
	}
}