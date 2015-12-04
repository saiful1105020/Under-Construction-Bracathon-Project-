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
		  
		if(isset($_SESSION["user_id"]))
		{
			die("Please Log out from your user account. ");
		}
		else
		{
			if(!isset($_SESSION["admin_id"]))
			{
				redirect('/home', 'refresh');
			}
		}
		  
		//load models
		
		$this->load->view('templates/header');
     }
	 
	/**
	*	[ADMIN HOME PAGE]
	*/
	
	public function index()			
	{
		
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