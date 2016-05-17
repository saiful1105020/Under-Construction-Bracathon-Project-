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
				$post['user_rating']=$this->post_model->get_current_rating($p['user_id']);
				
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
		
		//echo '<br><br><br>';
		//print_r($data['existingCat']);
		
		$this->load->view('addCategory',$data);
	}
	
	/**
	To-Do
	*/
	public function addCategoryAction($id)
	{
		
	}
	
	/**
	To-Do
	*/
	public function addNewCategory()
	{
		echo '<br><br><br>';
		echo $_POST['newCat'];
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
		echo '<br><br><br>'.$id;
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
		
		echo '<br><br><br>';
		print_r($existingCat);
		
		$data['mapData'] = $existingCat;
		
		//unset($data['existingCat'][0]);
		
		$this->load->view('showMap',$data);
	}
}