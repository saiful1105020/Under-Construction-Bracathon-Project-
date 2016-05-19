<?php
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
		  $this->load->model('admin_model');
		  $this->load->model('log_model');
     }
	 
	public function index()
	{
		
		if(isset($_SESSION["admin_name"]))
		{
			redirect('/admin', 'refresh');
		}
		else
		{
			$data = array(
               'login_error' => false
			);
			$this->load->view('admin-login',$data);
			//$this->load->view('newLogin1');
		}
	}
	
	public function login()		
	{
		if(isset($_POST['admin_name']) && isset($_POST['password']))
		{
			$data = array('admin_name'=>trim($_POST['admin_name']),'password'=>md5($_POST["password"]));
			
			$query= $this->admin_model->get_loginInfo($data);
			
			
			if($query->num_rows()==1)
			{
				$loginInfo=$query->row_array();
				$_SESSION["admin_name"]=$loginInfo['admin_name'];
				$_SESSION["admin_id"] = $this->admin_model->getAdminId($_SESSION["admin_name"]);
				//echo $_SESSION["admin_id"];
				//Load User Admin Page

				$logData = array();
		
				$logData['user_id']=$_SESSION["admin_id"];
				$logData['cat_id']=-1;
				$logData['log_type']=3;
				$logData['post_id']=-1;
				$logData['changed_status']=-1;
				$logData['cat_name']='';
				
				$this->log_model->insert_log($logData);

				redirect('/admin', 'refresh');
			}
			else
			{
				$data = array(
				   'login_error' => true
				);
				$this->load->view('admin-login',$data);
			}
			
		}
		else
		{
			$data = array(
				   'login_error' => false
				);
			$this->load->view('admin-login',$data);
		}
		
	}
}
