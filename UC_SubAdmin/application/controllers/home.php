<?php

/**
	Done
*/
defined('BASEPATH') OR exit('No direct script access allowed');

class Home extends CI_Controller {
	 
	 public function __construct()		//DONE
     {
          parent::__construct();
		  
		  //Load Necessary Libraries and helpers
          
		  $this->load->library('session');
          $this->load->helper('form');
          $this->load->helper('url');
          $this->load->helper('html');
		  $this->load->library('form_validation');
		  
		  // <Load Admin Model>
		  $this->load->model('subAdminModel');
		  $this->load->model('log_model');
     }
	 
	public function index()
	{
		
		if(isset($_SESSION["subAdminName"]))
		{
			redirect('/subAdmin', 'refresh');
		}
		else
		{
			$data = array(
               'login_error' => false
			);
			$this->load->view('subAdminLogin',$data);
			//$this->load->view('newLogin1');
		}
	}
	
	
	public function login()		
	{
		if(isset($_POST['admin_name']) && isset($_POST['password']))
		{
			//echo $_POST['admin_name'];
			
			$data = array('admin_name'=>trim($_POST['admin_name']),'password'=>md5($_POST["password"]));
			
			$query= $this->subAdminModel->get_loginInfo($data);
			
			//echo $query->num_rows();
			
			
			if($query->num_rows()==1)
			{
				$loginInfo=$query->row_array();
				$_SESSION["subAdminName"]=$loginInfo['name'];
				$_SESSION["subAdmin_id"] = $this->subAdminModel->getAdminId($_SESSION["subAdminName"]);
				$logData = array();
		
				$logData['user_id']=$_SESSION["subAdmin_id"];
				$logData['cat_id']=-1;
				$logData['log_type']=8;
				$logData['post_id']=-1;
				$logData['changed_status']=-1;
				$logData['cat_name']='';
				
				$this->log_model->insert_log($logData);
				
				//Load User Admin Page
				redirect('/subAdmin', 'refresh');
			}
			else
			{
				$data = array(
				   'login_error' => true
				);
				$this->load->view('subAdminLogin',$data);
			}
			
		}
		else
		{
			$data = array(
				   'login_error' => false
				);
			$this->load->view('subAdminLogin',$data);
		}
		
	}
	
}
