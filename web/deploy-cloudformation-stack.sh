#!/bin/bash
set -euo pipefail
IFS=$'\n\t'

CFN_STACK_NAME=$1
CFN_PARAMETERS_FILE=$2

STACK_STATUS=$(aws cloudformation describe-stacks --stack-name $CFN_STACK_NAME | jq -r '.Stacks[0].StackStatus' || echo none)

echo "Deploy CFN: stack_name=$CFN_STACK_NAME parameters_file=$CFN_PARAMETERS_FILE current_stack_status=$STACK_STATUS"

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
  aws cloudformation deploy \
    --stack-name $CFN_STACK_NAME \
    --template-file cfn-template.yml \
    --no-fail-on-empty-changeset
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
  exit 1
fi
