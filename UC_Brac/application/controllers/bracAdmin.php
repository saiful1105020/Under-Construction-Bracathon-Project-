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
		//print_r($temp);
		
		for($i=0;$i<50;$i++)
		{
			$idx = $index*50+$i;
			if($idx>=sizeof($temp)) break;
			
			$data['message'] = $this->form_string($temp[$idx]);
			echo $data['message'].'    -------    '.$temp[$idx]['time'];
			//print_r($temp[$idx]);
			echo '<br>';
			
			//$this->load->view('bracHome');
		}
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
		
		//$this->session->sess_destroy();	//!Stop Session 
		
		unset($_SESSION["bracAdmin_id"]);
		
		/**
		*Redirect To Homepage
		*/
		
		redirect('/home', 'refresh');
	}
}