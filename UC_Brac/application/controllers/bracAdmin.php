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
		}

		$data['index'] = $index;
		$data['logData'] =$logArray;
		$this->load->view('bracHome',$data);
	}
	
	public function form_string($log)
	{
		$str = "";
		
		if($log['log_type']==2)
		{
			$str = "User <b>".$this->log_model->get_user_name($log['user_id'])."</b> (<font color='red'>".$this->log_model->get_user_rating($log['user_id'])."</font>) Reported a problem";
		}
		else if($log['log_type']==3)
		{
			$str = "Admin <b>".$this->log_model->get_admin_name($log['user_id'])."</b> logged in";
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
			$str = "Admin <b>".$this->log_model->get_admin_name($log['user_id'])."</b> marked a post as <font color='blue'>".$tempstr[$log['changed_status']]."</font>";
			
			//print_r($tempstr);
		}
		else if($log['log_type']==5)
		{
			$str = "Admin <b>".$this->log_model->get_admin_name($log['user_id'])."</b> logged out";
		}
		else if($log['log_type']==6)
		{
			$str = "Admin <b>".$this->log_model->get_admin_name($log['user_id'])."</b> added a new category : <font color='blue'>".$log['cat_name'].'</font>';
		}
		else if($log['log_type']==7)
		{
			$str = "Admin <b>".$this->log_model->get_admin_name($log['user_id'])."</b> deleted a category : <font color='blue'>".$log['cat_name'].'</font>';
		}
		else if($log['log_type']==8)
		{
			$str = "Sub-Admin <b>".$this->log_model->get_subAdmin_name($log['user_id'])."</b> logged in";
		}
		else if($log['log_type']==9)
		{
			$str = "Sub-Admin <b>".$this->log_model->get_subAdmin_name($log['user_id'])."</b> logged out";
		}
		else if($log['log_type']==10)
		{
			$tempstr = array(0=>"<b><font color='green'>UNFLAGGED</font></b>",1=>"<font color='red'>FLAGGED</font>");
			$str = "Sub-Admin <b>".$this->log_model->get_subAdmin_name($log['user_id'])."</b> marked a post as ".$tempstr[$log['changed_status']];
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

	public function changePassword()
	{
		$this->load->view('changePassword');
	}
	
	public function changePassAction()
	{
		echo '<br><br><br>';
		$oldPassword = md5($_POST['oldPassword']);
		$newPassword = md5($_POST['newPassword']);
		$confirmPassword = md5($_POST['confirmPassword']);
		
		//check password match
		if($this->bracAdminModel->checkPassword($_SESSION['bracAdminName'],$oldPassword)==1)
		{
			//check new and confirm
			if($newPassword === $confirmPassword)
			{
				//change password
				$this->bracAdminModel->changePassword($_SESSION['bracAdminName'],$newPassword);
				$data['success']=true;
				$data['success_message']='Password changed successfully';
				$this->load->view('status_message',$data);
			}
			else
			{
				$data['success']=false;
				$data['fail_message']='Password and Confirm Password not matched. Please Try Again.';
				$this->load->view('status_message',$data);
			}
		}
		else
		{
			$data['success']=false;
			$data['fail_message']='Wrong Password. Please Try Again Later.';
			$this->load->view('status_message',$data);
		}
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
		
		unset($_SESSION["bracAdminName"]);
		
		/**
		*Redirect To Homepage
		*/
		
		redirect('/home', 'refresh');
	}
	
	public function addAdmin()
	{
		//echo '<br><br><br>';
		$data['adminList'] = $this->bracAdminModel->getAdminList();
		//print_r($data);
		
		//generate list value:id, text: name
		$this->load->view('addAdmin',$data);
	}
	
	public function addAdminAction()
	{
		echo '<br><br><br>';
		
		$name = $_POST['name'];
		$password = md5($_POST['password']);
		
		$this->bracAdminModel->addAdmin($name,$password);
		
		$data['success']=true;
		$data['success_message']='Admin added successfully';
		$this->load->view('status_message',$data);
	}
	
	public function deleteAdminAction($id)
	{
		echo '<br><br><br>';
		
		$this->bracAdminModel->deleteAdmin($id);
		$data['success']=true;
		$data['success_message']='Sub-Admin deleted successfully';
		$this->load->view('status_message',$data);
	}
}
