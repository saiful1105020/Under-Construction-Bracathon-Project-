<?php

/**
*	Controller for all admin actions
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
		 
		/**
			If not logged in, redirect to login page
		*/
		if(!isset($_SESSION["admin_name"]))
		{
			redirect('/home', 'refresh');
		}
		  
		//load models
		$this->load->model('admin_model');
		$this->load->model('log_model');
		$this->load->model('post_model');
		
		$this->load->view('templates/header2');
     }
	 
	
	
	/**
	*	[ADMIN HOME PAGE] --- Search is the default activity
	*/ 
	public function index()			
	{
		redirect('admin/search','refresh');
	}
	
	public function insert_log_test()
	{
		$data = array();
		
		$data['user_id']=1;
		$data['cat_id']=2;
		$data['time']='2015-12-06 02:00:00';
		$data['log_type']=3;
		$data['post_id']=2;
		$data['prev_status']=3;
		$data['changed_status']=1;
		$data['cat_name']="Test";
		
		$this->log_model->insert_log($data);
	}
	
	public function search()
	{
		if(!isset($_POST['location_select']))
		{
			$data['is_set']=false;
			$data['n_loc']=$this->admin_model->get_all_locations();
			
			$data['catData'] = $this->post_model->get_all_categories();
			
			//echo '<br><br><br>';
			//print_r($data['catData']);
			
			
			$this->load->view('adminhome',$data);
		}
		else
		{
			$data['n_loc']=$this->admin_model->get_all_locations();
			$data['catData'] = $this->post_model->get_all_categories();
			
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
				//echo '<br><br><br>';
				//echo $p['category'];
				
				$post=$p;
				$temp=$this->post_model->get_vote_count($p['post_id']);
				$post['up_votes']=$temp['upvotes'];
				$post['down_votes']=$temp['downvotes'];
				$post['user_name']=$this->post_model->get_user_name($p['user_id']);
				$post['user_rating']=$this->post_model->get_current_rating($p['user_id']);
				
				//echo '<br><br><br>'.$p['category'];
				
				//if(!($p['category']>=-1)) echo 'ERROR';
				
				$post['cat_name'] = $this->post_model->get_category_name($p['category']);
				
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
		$data['success_message']="Status and user voteCount updated successfully";

		$logData = array();
		
		$logData['user_id']=$_SESSION["admin_id"];
		$logData['cat_id']=$this->post_model->getCategoryId($post_id);
		$logData['log_type']=4;
		$logData['post_id']=$post_id;
		$logData['changed_status']=$action;
		$logData['cat_name']=$this->post_model->get_category_name($logData['cat_id']);
		
		$this->log_model->insert_log($logData);

		$this->load->view('status_message',$data);
	}
	
	/**
	*	Admin Logs out
	*/
	
	public function logout()	
	{
		$logData = array();
		
		$logData['user_id']=$_SESSION["admin_id"];
		$logData['cat_id']=-1;
		$logData['log_type']=5;
		$logData['post_id']=-1;
		$logData['changed_status']=-1;
		$logData['cat_name']='';
				
		$this->log_model->insert_log($logData);

		$this->session->sess_destroy();	//!Stop Session 
		
		/**
		*Redirect To Homepage
		*/
		
		redirect('/home', 'refresh');
	}
	/**
	neamul
	*/
	public function showBarGraph()
	{
		$catIds = $this->post_model->get_category_ids();
		$cats = array();
		$pCount = array();
		$solvedCount = array();
		$i = 0;
		foreach($catIds as $id)
		{
			$newCat[0] = "Avoid This";
			$newCat[1] = $this->post_model->get_category_name($id);
			
			$count = $this->post_model->category_problem_count($id);
			
			$sCount = $this->post_model->category_solved_count($id);
			
			$cats[$i]=$newCat;
			$pCount[$i] = $count;
			$solvedCount[$i] = $sCount;
			
			$i++;
		}
		
		//echo '<br><br><br><br><br><br><br>';
		
		//print_r($cats);
		
		//echo '<br>';
		//print_r($pCount);
		
		/*
		$pCounts = $this->post_model->get_problem_count();
		$temp = $this->post_model->get_all_categories();
		$cats = array();
		
		foreach($temp as $t)
		{
			$newCat = array();
			$newCat[0] = $t['categoryId'];
			$newCat[1] = $t['name'];
			
			array_push($newCat, $cats);
		}
		*/
		//print_r($cats);
		
		
		$data['pCounts'] = $pCount;
		$data['cats'] = $cats;
		$data['solvedCounts'] = $solvedCount;
		
		//echo '<br><br><br><br><br><br><br>';
		
		//print_r($solvedCount);

		$locations = array();	
		$lCounts = array();
		$solvedCounts2 = array();


		$nbrhood = $this->post_model->get_all_neighborhoods();
		foreach ($nbrhood as $n) {
			//echo $n['neighbourhood'];
			$element = array();
			//$element['name']=$n['neighbourhood'];
			$element['loc_id']=$this->post_model->get_nbrhd_location_ids($n['neighbourhood']);
			$element['problem_count'] = $this->post_model->get_problem_count_location($element['loc_id']);
			$element['solved_count'] = $this->post_model->get_solved_count_location($element['loc_id']);
			//array_push($data,$element);


			array_push($locations,$n['neighbourhood']);				//
			array_push($lCounts, $element['problem_count']);	//
			array_push($solvedCounts2, $element['solved_count']);

		}

		//$pCounts = $this->post_model->get_problem_count();
		//$cats = array(array(0,'Occupied Footpath'), array(1,'Open Dustbin'), array(2,'Open Manhole'), array(3,'Cluttered Electric Wires'), array(4,'Waterlogging'), array(5,'Risky Intersection'), array(6,'No Street Light'), array(7,'Crime Prone Area'), array(8,'Damaged Road'), array(9,'Wrong Way Traffic'));
		$data['lCounts'] = $lCounts;
		$data['locations'] = $locations;
		$data['solvedCounts2'] = $solvedCounts2;
		
		
		//echo '<br><br><br><br>';
		//print_r($data);
		
		$this->load->view("showBarGraph", $data);
		
	}

	public function test()
	{
		//$data = array();


		$locations = array();	//
		$lCounts = array();	//


		$nbrhood = $this->post_model->get_all_neighborhoods();
		foreach ($nbrhood as $n) {
			//echo $n['neighbourhood'];
			$element = array();
			$element['name']=$n['neighbourhood'];
			$element['loc_id']=$this->post_model->get_nbrhd_location_ids($n['neighbourhood']);
			$element['problem_count'] = $this->post_model->get_problem_count_location($element['loc_id']);
			//array_push($data,$element);


			array_push($locations,$n['neighbourhood']);				//
			array_push($lCounts, $element['problem_count']);	//

		}

		//$pCounts = $this->post_model->get_problem_count();
		//$cats = array(array(0,'Occupied Footpath'), array(1,'Open Dustbin'), array(2,'Open Manhole'), array(3,'Cluttered Electric Wires'), array(4,'Waterlogging'), array(5,'Risky Intersection'), array(6,'No Street Light'), array(7,'Crime Prone Area'), array(8,'Damaged Road'), array(9,'Wrong Way Traffic'));
		$data['lCounts'] = $lCounts;
		$data['locations'] = $locations;
		$this->load->view("showBarGraph", $data);
		//print_r($data);
	}

	public function addCategory()
	{
		$catData = $this->post_model->get_suggested_categories();

		$data['catData'] = $catData;
		$data['existingCat'] = $this->post_model->get_all_categories();
		
		unset($data['existingCat'][0]);
		
		//ksort($data['existingCat']);
		
		//echo '<br><br><br>';
		//print_r($data['existingCat']);
		
		$this->load->view('addCategory',$data);
	}
	
	/**
	To-Do
	*/
	public function addCategoryAction($id)
	{
		//get category name from suggestedCategories table
		$cat_name = $this->post_model->get_suggested_cat_name($id);
		
		//remove it from suggestedCategories table
		$this->post_model->delete_suggested_cat($id);
		
		//insert it into categories table
		$this->post_model->insert_cat($cat_name);
		
		$data['success']=true;
		$data['success_message']='Category added successfully.';

		$logData = array();
		
		$logData['user_id']=$_SESSION["admin_id"];
		$logData['cat_id']=$this->post_model->get_category_id_from_name($cat_name);
		$logData['log_type']=6;
		$logData['post_id']=-1;
		$logData['changed_status']=-1;
		$logData['cat_name']=$cat_name;
		
		$this->log_model->insert_log($logData);

		$this->load->view('status_message',$data);
	}
	
	/**
	To-Do
	*/
	public function addNewCategory()
	{
		$cat_name = $_POST['newCat'];
		//insert it into categories table
		$this->post_model->insert_cat($cat_name);
		
		$data['success']=true;
		$data['success_message']='Category added successfully.';

		$logData = array();
		
		$logData['user_id']=$_SESSION["admin_id"];
		$logData['cat_id']=$this->post_model->get_category_id_from_name($cat_name);
		$logData['log_type']=6;
		$logData['post_id']=-1;
		$logData['changed_status']=-1;
		$logData['cat_name']=$cat_name;
		
		$this->log_model->insert_log($logData);

		$this->load->view('status_message',$data);
	}
	

	public function deleteCategory()
	{
		$data['existingCat'] = $this->post_model->get_all_categories();
		
		unset($data['existingCat'][0]);
		
		//echo '<br><br><br>';
		//print_r($data['existingCat']);
		
		$this->load->view('deleteCategory',$data);
	}
	
	/**
	To-Do
	*/
	public function deleteCategoryAction($id)
	{
		$cat_name = $this->post_model->get_category_name($id);
		$this->post_model->delete_cat($id);
		
		$data['success']=true;
		$data['success_message']='Category deleted successfully.';

		$logData = array();
		
		$logData['user_id']=$_SESSION["admin_id"];
		$logData['cat_id']=-1;
		$logData['log_type']=7;
		$logData['post_id']=-1;
		$logData['changed_status']=-1;
		$logData['cat_name']=$cat_name;
		
		$this->log_model->insert_log($logData);

		$this->load->view('status_message',$data);
	}

	public function showMap()
	{
		$temp = $this->post_model->get_all_categories();
		$existingCat = array();
		
		foreach($temp as $t)
		{
			$e = array();
			$e['id'] = $t['categoryId'];
			$e['name'] = $t['name'];
			$e['locations'] = $this->post_model->get_category_problem_locations($e['id']);
			
			array_push($existingCat,$e);
		}
		
		//echo '<br><br><br>';
		//print_r($existingCat);
		
		$data['mapData'] = $existingCat;
		
		//unset($data['existingCat'][0]);
		
		$this->load->view('showMap',$data);
	}
	
	public function showALocation($id)
	{
		//$location['lat'] = 87.091;
		//$location['lon'] = 90.098;
		$data['location'] = $this->post_model->get_post_location($id);
		
		//echo '<br><br><br>';
		//print_r($data['location']);
		
		$this->load->view('showALocation',$data);
	}
}