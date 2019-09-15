#!/bin/bash
set -euo pipefail
IFS=$'\n\t'

CFN_STACK_NAME=$1
CFN_PARAMETERS_FILE=$2

STACK_STATUS=$(aws cloudformation describe-stacks --stack-name $CFN_STACK_NAME | jq -r '.Stacks[0].StackStatus' || echo none)

echo $CFN_STACK_NAME $CFN_PARAMETERS_FILE $STACK_STATUS

if [[ $STACK_STATUS == "none" ]]; then
  echo "Creating $CFN_STACK_NAME"
  aws cloudformation create-stack \
    --stack-name $CFN_STACK_NAME \
    --template-body file://./cfn-template.yml \
    --parameters file://./$CFN_PARAMETERS_FILE
  echo "Waiting $CFN_STACK_NAME"
  aws cloudformation wait stack-create-complete --stack-name $CFN_STACK_NAME
elif [[ $STACK_STATUS == "UPDATE_COMPLETE" || $STACK_STATUS == "CREATE_COMPLETE" ]]; then
  echo "Updating $CFN_STACK_NAME"
  aws cloudformation update-stack \
    --stack-name $CFN_STACK_NAME \
    --template-body file://./cfn-template.yml \
    --parameters file://./$CFN_PARAMETERS_FILE
  echo "Waiting $CFN_STACK_NAME"
  aws cloudformation wait stack-update-complete --stack-name $CFN_STACK_NAME
elif [[ $STACK_STATUS == "ROLLBACK_COMPLETE" ]]; then
  echo "Deleting $CFN_STACK_NAME"
  aws cloudformation delete-stack --stack-name $CFN_STACK_NAME
  echo "Creating $CFN_STACK_NAME"
  aws cloudformation create-stack \
    --stack-name $CFN_STACK_NAME \
    --template-body file://./cfn-template.yml \
    --parameters file://./$CFN_PARAMETERS_FILE
  echo "Waiting $CFN_STACK_NAME"
  aws cloudformation wait stack-create-complete --stack-name $CFN_STACK_NAME
else
  echo "Unhandled stack status $STACK_STATUS"
fi
